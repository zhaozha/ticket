package com.qy.ticket.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qy.ticket.common.CommonResult;
import com.qy.ticket.config.ValidatorImpl;
import com.qy.ticket.constant.RedisConstant;
import com.qy.ticket.dao.*;
import com.qy.ticket.dto.user.TblBillDTO;
import com.qy.ticket.dto.user.TblRefundDTO;
import com.qy.ticket.dto.user.TblSpecialRefundDTO;
import com.qy.ticket.dto.user.TblTicketDTO;
import com.qy.ticket.dto.wx.*;
import com.qy.ticket.entity.*;
import com.qy.ticket.service.UserService;
import com.qy.ticket.util.JwtUtil;
import com.qy.ticket.util.ValidationResult;
import com.qy.ticket.util.WXPayUtil;
import com.virgo.virgoidgenerator.intf.IdBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.qy.ticket.constant.SystemConstant.*;
import static com.qy.ticket.util.WXPayConstants.DOMAIN_API;
import static com.qy.ticket.util.WXPayConstants.REFUND_URL_SUFFIX;
import static com.qy.ticket.util.WXPayConstants.UNIFIEDORDER_URL_SUFFIX;

/**
 * @author zhaozha
 * @date 2020/1/7 上午10:59
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    @Value("${tx.wx.appid}")
    private String WX_APP_ID;

    @Value("${tx.wx.appsecret}")
    private String WX_APP_SECRET;

    @Value("${tx.wx.pay.url}")
    private String WX_PAY_URL;

    @Value("${tx.wx.pay.key}")
    private String WX_PAY_KEY;

    @Value("${tx.wx.msg.templateid}")
    private String WX_MSG_TEMPLATEID;

    @Value("${tx.wx.pay.mch}")
    private String WX_PAY_MCH;

    @Value("${jwt.expire-time-in-second}")
    private Long expirationTimeInSecond;

    private final TblUserMapper tblUserMapper;
    private final TblBillMapper tblBillMapper;
    private final TblBillChildMapper tblBillChildMapper;
    private final TblRecordMapper tblRecordMapper;
    private final TblTicketMapper tblTicketMapper;
    private final VTicketMapper vTicketMapper;
    private final TblBillRefundMapper tblBillRefundMapper;
    private final TblCheckMapper tblCheckMapper;
    private final VCheckMapper vCheckMapper;

    private final ValidatorImpl validator;
    private final IdBaseService idBaseService;
    private final RestTemplate restTemplate;
    private final RestTemplate wxRefundRestTemplate;
    private final RedissonClient redissonSingle;
    private final JwtUtil jwtOperator;

    @Override
    public CommonResult wxLogin(String code) {
        String url =
                "https://api.weixin.qq.com/sns/jscode2session?appid="
                        + WX_APP_ID
                        + "&secret="
                        + WX_APP_SECRET
                        + "&js_code="
                        + code
                        + "&grant_type=authorization_code";

        String result = restTemplate.getForObject(url, String.class);
        String openId = JSONObject.parseObject(result).getString("openid");
        String sessionKey = JSONObject.parseObject(result).getString("session_key");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", openId);

        Example example = new Example(TblUser.class, true, true);
        example.createCriteria().andEqualTo("openId", openId);
        List<TblUser> tblUsers = tblUserMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(tblUsers)) {
            jsonObject.put("phoneNum", "");
            jsonObject.put("id", "");
            jsonObject.put("sessionKey", sessionKey);
            return CommonResult.builder().status(400).msg("未注册").data(jsonObject).build();
        }

        TblUser tblUser = tblUsers.get(0);
        jsonObject.put("phoneNum", tblUser.getPhoneNum());
        jsonObject.put("id", tblUser.getId());
        jsonObject.put("sessionKey", sessionKey);
        // token
        RBucket<String> bucket = redissonSingle.getBucket(RedisConstant.concat(RedisConstant.KEY_USER_TOKEN, tblUser.getId().toString()));
        Map<String, Object> map = new HashMap<>();
        map.put(CONTEXT_KEY_USER_ID, tblUser.getId());
        map.put(CONTEXT_KEY_USER_NAME, tblUser.getNickName());
        map.put(CONTEXT_KEY_USER_PHONE, tblUser.getPhoneNum());
        map.put(CONTEXT_KEY_USER_OPEN_ID, tblUser.getOpenId());
        String token = jwtOperator.generateToken(map);
        bucket.set(token, expirationTimeInSecond, TimeUnit.SECONDS);
        jsonObject.put("token", token);

        return CommonResult.builder().status(200).msg("查询成功").data(jsonObject).build();
    }

    @Override
    public CommonResult wxRegister(TblUser tblUser) {
        String phoneNum = tblUser.getPhoneNum();
        if (StringUtils.isEmpty(phoneNum)) {
            return CommonResult.builder().status(400).msg("手机号必须填写").data(tblUser).build();
        }
        tblUser.setId(idBaseService.genId());
        tblUserMapper.insert(tblUser);
        return CommonResult.builder().status(200).msg("注册成功").data(tblUser).build();
    }

    @Override
    @Transactional
    public CommonResult unifiedorder(TblBillDTO tblBillDTO) throws Exception {
        Long billId = idBaseService.genId();
        // 统一下单
        WxUnifiedorderDTO wxUnifiedorderDTO =
                WxUnifiedorderDTO.builder()
                        .appid(WX_APP_ID)
                        .mch_id(WX_PAY_MCH)
                        .openid(tblBillDTO.getOpenId())
                        .body("景区票务")
                        .nonce_str(WXPayUtil.generateNonceStr())
                        .notify_url(WX_PAY_URL)
                        .out_trade_no(String.valueOf(billId))
                        .total_fee(tblBillDTO.getAmount().intValue() * 100)
                        .trade_type("JSAPI")
                        .build();
        String retStr = restTemplate.postForObject(
                DOMAIN_API + UNIFIEDORDER_URL_SUFFIX,
                WXPayUtil.generateSignedXml(WXPayUtil.objectToMap(wxUnifiedorderDTO), WX_PAY_KEY),
                String.class);
        WxUnifiedorderResultDTO wxUnifiedorderResultDTO =
                (WxUnifiedorderResultDTO)
                        WXPayUtil.mapToObject(WXPayUtil.xmlToMap(retStr), WxUnifiedorderResultDTO.class);
        // 业务逻辑
        if (wxUnifiedorderResultDTO.getResult_code().equals("SUCCESS")
                && wxUnifiedorderResultDTO.getReturn_code().equals("SUCCESS")) {
            WxPayResultDTO wxPayResultDTO = WxPayResultDTO.builder()
                    .appId(WX_APP_ID)
                    .nonceStr(WXPayUtil.generateNonceStr())
                    .wxPackage("prepay_id=" + wxUnifiedorderResultDTO.getPrepay_id())
                    .signType("MD5")
                    .timeStamp(String.valueOf(WXPayUtil.getCurrentTimestampMs()))
                    .build();
            wxPayResultDTO.setPaySign(WXPayUtil.xmlToMap(
                    WXPayUtil.generateSignedXml(WXPayUtil.objectToMap(wxPayResultDTO), WX_PAY_KEY))
                    .get("sign"));
            // 主订单
            List<TblTicketDTO> list = tblBillDTO.getList().stream()
                    .filter(s -> s.getTicketNum() > 0)
                    .collect(Collectors.toList());
            TblTicket tblTicket = tblTicketMapper.selectByPrimaryKey(list.get(0).getTicketId());
            TblBill tblBill = new TblBill();
            BeanUtils.copyProperties(tblBillDTO, tblBill);
            tblBill.setTime(new Date());
            tblBill.setId(billId);
            tblBill.setParkId(tblTicket.getParkId());
            tblBill.setProductId(tblTicket.getProductId());
            // 设置为未支付状态
            tblBill.setStatus(0);
            tblBill.setRefundAmount(new BigDecimal("0"));
            tblBillMapper.insert(tblBill);

            // 详细清单 & 校验
            BigDecimal amount = tblBill.getAmount();
            for (TblTicketDTO tblTicketDTO : list) {
                Long ticketId = tblTicketDTO.getTicketId();
                TblTicket tblTicket1 = tblTicketMapper.selectByPrimaryKey(ticketId);
                // 合计
                BigDecimal total =
                        tblTicket1.getPrice().multiply(new BigDecimal(tblTicketDTO.getTicketNum()));
                amount = amount.subtract(total);
                TblBillChild tblBillChild = new TblBillChild();
                BeanUtils.copyProperties(tblBill, tblBillChild);
                BeanUtils.copyProperties(tblTicketDTO, tblBillChild);
                tblBillChild.setAmount(total);
                tblBillChild.setTicketPrice(tblTicket1.getPrice());
                tblBillChild.setId(idBaseService.genId());
                tblBillChild.setBillId(tblBill.getId());
                tblBillChild.setFatherAmount(tblBill.getAmount());
                tblBillChildMapper.insert(tblBillChild);
            }
            if (amount.intValue() != 0) {
                return CommonResult.builder().status(400).msg("支付参数有误").build();
            }
            return CommonResult.builder().status(200).msg("下单成功").data(wxPayResultDTO).build();
        }
        return CommonResult.builder().status(400).msg("下单失败,请您重试").build();
    }

    @Override
    @Transactional
    public String wxPayConfirm(String xmlStr) throws Exception {
        WxPayConformDTO wxPayConform =
                (WxPayConformDTO) WXPayUtil.mapToObject(WXPayUtil.xmlToMap(xmlStr), WxPayConformDTO.class);
        if (wxPayConform.getResult_code().equals("SUCCESS")
                && wxPayConform.getReturn_code().equals("SUCCESS")) {
            long billId = Long.parseLong(wxPayConform.getOut_trade_no());
            // 处理账单
            TblBill tblBill = tblBillMapper.selectByPrimaryKey(billId);
            tblBill.setStatus(1);
            tblBillMapper.updateByPrimaryKey(tblBill);
            Long userId = tblBill.getUserId();
            String phoneNum = tblBill.getPhoneNum();
            Example example = new Example(TblRecord.class, true, true);
            example
                    .createCriteria()
                    .andLike("time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "%")
                    .andEqualTo("phoneNum", phoneNum);
            List<TblRecord> tblRecords = tblRecordMapper.selectByExample(example);
            example.clear();
            example = new Example(TblBillChild.class, true, true);
            example.createCriteria().andEqualTo("billId", billId);
            List<TblBillChild> tblBillChildren = tblBillChildMapper.selectByExample(example);
            for (TblBillChild tblBillChild : tblBillChildren) {
                // 如果已经购买的再次购买、锁住行程进行添加
                long recordId = idBaseService.genId();
                boolean flag = true;
                if (!tblRecords.isEmpty()) {
                    for (TblRecord tblRecord : tblRecords) {
                        if (tblBillChild.getTicketId().longValue() == tblRecord.getTicketId().longValue()) {
                            // 锁住行程、避免多线程干扰
                            RLock lock =
                                    redissonSingle.getLock("gate-record-" + String.valueOf(tblRecord.getId()));
                            lock.lock(120, TimeUnit.SECONDS);
                            updateRecord(tblRecord, tblBillChild);
                            lock.unlock();
                            flag = false;
                            recordId = tblRecord.getId();
                        }
                    }
                }
                // 如果没有、则进行行程添加
                if (flag) {
                    example.clear();
                    example = new Example(VTicket.class, true, true);
                    example.createCriteria().andEqualTo("ticketId", tblBillChild.getTicketId());
                    List<VTicket> vTickets = vTicketMapper.selectByExample(example);
                    VTicket vTicket = vTickets.get(0);
                    RAtomicLong rAtomicLong =
                            redissonSingle.getAtomicLong(
                                    "gate-seq-pdId"
                                            + vTicket.getProductId()
                                            + "-paId"
                                            + vTicket.getParkId()
                                            + "-time"
                                            + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    long seq = rAtomicLong.incrementAndGet();

                    tblRecordMapper.insert(
                            TblRecord.builder()
                                    .id(recordId)
                                    .amount(
                                            tblBillChild
                                                    .getTicketPrice()
                                                    .multiply(new BigDecimal(tblBillChild.getTicketNum())))
                                    .refundAmount(new BigDecimal("0"))
                                    .income(
                                            tblBillChild
                                                    .getTicketPrice()
                                                    .multiply(new BigDecimal(tblBillChild.getTicketNum())))
                                    .ticketId(tblBillChild.getTicketId())
                                    .time(new Date())
                                    .userId(userId)
                                    .availableNum(tblBillChild.getTicketNum())
                                    .totalNum(tblBillChild.getTicketNum())
                                    .usedNum(0)
                                    .phoneNum(tblBill.getPhoneNum())
                                    .versionId(1)
                                    .parkId(vTicket.getParkId())
                                    .ticketPrice(vTicket.getTicketPrice())
                                    .parkName(vTicket.getParkName())
                                    .productId(vTicket.getProductId())
                                    .productName(vTicket.getProductName())
                                    .ticketName(vTicket.getTicketName())
                                    .reason("")
                                    .returnableAmount(
                                            vTicket
                                                    .getReturnableAmount()
                                                    .multiply(new BigDecimal(tblBillChild.getTicketNum())))
                                    .effectiveNum(tblBillChild.getTicketNum())
                                    .seq(String.format("%03d", seq))
                                    .build());
                }
                tblBillChild.setStatus(1);
                tblBillChild.setRecordId(recordId);
                tblBillChildMapper.updateByPrimaryKey(tblBillChild);
            }

            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        }
        return null;
    }

    @Override
    public CommonResult ticket(Long productId, Long parkId) {
        Example example = new Example(VTicket.class, true, true);
        example.createCriteria().andEqualTo("productId", productId).andEqualTo("parkId", parkId);
        List<VTicket> vTickets = vTicketMapper.selectByExample(example);
        return CommonResult.builder().status(200).msg("查询成功").data(vTickets).build();
    }

    @Override
    public CommonResult record(String phoneNum, Integer status, Long productId, Long parkId) {
        return historyRecord(
                phoneNum, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), status, productId, parkId);
    }

    @Override
    public CommonResult historyRecord(
            String phoneNum, String date, Integer status, Long productId, Long parkId) {
        Example example = new Example(TblRecord.class, true, true);
        Example.Criteria criteria = example.createCriteria();
        if (0 == status) {
            criteria.andGreaterThan("availableNum", 0);
        }
        if (-1L != productId.longValue() || -1L != parkId.longValue()) {
            criteria.andEqualTo("productId", productId).andEqualTo("parkId", parkId);
        }
        criteria.andLike("time", date + "%").andEqualTo("phoneNum", phoneNum);
        List<TblRecord> tblRecords = tblRecordMapper.selectByExample(example);
        if (tblRecords.isEmpty()) {
            return CommonResult.builder().status(400).msg("无数据").build();
        }
        return CommonResult.builder().status(200).msg("查询成功").data(tblRecords).build();
    }

    // 判断整数
    private static Pattern pattern = Pattern.compile("[0-9]*");

    private static boolean isNumeric(String str) {
        return pattern.matcher(str).matches();
    }

    @Override
    @Transactional
    public CommonResult refund(TblRefundDTO tblRefundDTO) throws Exception {
        // 校验入参
        ValidationResult result = validator.validate(tblRefundDTO);
        if (result.isHasErrors()) {
            return CommonResult.builder().status(400).msg("退款参数有误").build();
        }
        // 票数必须是整数
        if (!isNumeric(tblRefundDTO.getTicketNum() + "")) {
            return CommonResult.builder().status(400).msg("票数必须是整数").build();
        }
        // 票数合规检测
        Long recordId = tblRefundDTO.getRecordId();
        RLock refundLock = redissonSingle.getLock("gate-refund-" + recordId);
        if (!refundLock.tryLock(0, 60, TimeUnit.SECONDS)) {
            return CommonResult.builder().status(400).msg("正在退款,请稍等").build();
        }
        Integer ticketNum = tblRefundDTO.getTicketNum();
        TblRecord tblRecord = tblRecordMapper.selectByPrimaryKey(recordId);
        if (tblRecord.getAvailableNum() < ticketNum) {
            refundLock.unlock();
            return CommonResult.builder().status(400).msg("超过可退票数").build();
        }
        // 退款金额计算
        BigDecimal ticketPrice = tblRecord.getTicketPrice();
        BigDecimal totalRefundAmount = ticketPrice.multiply(new BigDecimal(String.valueOf(ticketNum)));
        if (tblRecord.getIncome().compareTo(totalRefundAmount) == -1) {
            refundLock.unlock();
            return CommonResult.builder().status(400).msg("退款金额有误").build();
        }
        CommonResult commonResult = circleRefund(tblRecord, totalRefundAmount, true, false);
        // 退款成功修改有效票数
        if (commonResult.getStatus() == 200) {
            tblRecord = tblRecordMapper.selectByPrimaryKey(recordId);
            tblRecord.setEffectiveNum(tblRecord.getEffectiveNum() - ticketNum);
            tblRecordMapper.updateByPrimaryKey(tblRecord);
        }
        refundLock.unlock();
        return commonResult;
    }


    @Override
    @Transactional
    public CommonResult specialRefund(TblSpecialRefundDTO tblSpecialRefundDTO) throws Exception {
        // 校验入参
        ValidationResult result = validator.validate(tblSpecialRefundDTO);
        if (result.isHasErrors()) {
            return CommonResult.builder().status(400).msg("退款参数有误").build();
        }
        // 金额合规检测
        Long recordId = tblSpecialRefundDTO.getRecordId();
        BigDecimal refundAmount = tblSpecialRefundDTO.getRefundAmount();
        RLock refundLock = redissonSingle.getLock("gate-refund-" + recordId);
        if (!refundLock.tryLock(0, 60, TimeUnit.SECONDS)) {
            return CommonResult.builder().status(400).msg("正在退款,请稍等").build();
        }
        TblRecord tblRecord = tblRecordMapper.selectByPrimaryKey(recordId);
        if (null == tblRecord) {
            refundLock.unlock();
            return CommonResult.builder().status(400).msg("票信息有误").build();
        }
        if (tblRecord.getIncome().compareTo(refundAmount) == -1) {
            refundLock.unlock();
            return CommonResult.builder().status(400).msg("退款金额有误").build();
        }
        // 如果是退押金引发的指定金额退款、不记录原因
        CommonResult commonResult =
                circleRefund(
                        tblRecord,
                        refundAmount,
                        false,
                        null == tblSpecialRefundDTO.getFlag() ? true : tblSpecialRefundDTO.getFlag());
        refundLock.unlock();
        return commonResult;
    }

    private CommonResult circleRefund(
            TblRecord tblRecord,
            BigDecimal totalRefundAmount,
            Boolean cancellationFlag,
            Boolean reasonFlag)
            throws Exception {
        BigDecimal shouldRefundAmount = totalRefundAmount;
        // 循环退款
        Example example = new Example(TblBillChild.class, true, true);
        example.createCriteria().andEqualTo("recordId", tblRecord.getId()).andEqualTo("status", 1);
        List<TblBillChild> tblBillChildren = tblBillChildMapper.selectByExample(example);
        for (TblBillChild tblBillChild : tblBillChildren) {
            if (totalRefundAmount.compareTo(new BigDecimal("0")) < 1) {
                break;
            }
            // 本单可退
            BigDecimal billCanRefund = tblBillChild.getAmount().subtract(tblBillChild.getRefundAmount());
            if (billCanRefund.compareTo(new BigDecimal("0")) < 1) {
                continue;
            }
            BigDecimal billRealRefund = new BigDecimal("0");
            long refundId = idBaseService.genId();
            TblTicket tblTicket = tblTicketMapper.selectByPrimaryKey(tblBillChild.getTicketId());
            WxPayRefundDTO wxPayRefundDTO =
                    WxPayRefundDTO.builder()
                            .mch_id(WX_PAY_MCH)
                            .appid(WX_APP_ID)
                            .nonce_str(WXPayUtil.generateNonceStr())
                            .out_trade_no(String.valueOf(tblBillChild.getBillId()))
                            .out_refund_no(String.valueOf(refundId))
                            .total_fee(tblBillChild.getFatherAmount().multiply(new BigDecimal("100")).intValue())
                            .build();
            if (billCanRefund.compareTo(totalRefundAmount) < 1) {
                totalRefundAmount = totalRefundAmount.subtract(billCanRefund);
                billRealRefund = billCanRefund;
            } else {
                billRealRefund = totalRefundAmount;
                totalRefundAmount = new BigDecimal("0");
            }
            wxPayRefundDTO.setRefund_fee(billRealRefund.multiply(new BigDecimal("100")).intValue());
            WxPayRefundResultDTO wxPayRefundResultDTO =
                    (WxPayRefundResultDTO)
                            WXPayUtil.mapToObject(
                                    WXPayUtil.xmlToMap(
                                            wxRefundRestTemplate.postForObject(
                                                    DOMAIN_API + REFUND_URL_SUFFIX,
                                                    WXPayUtil.generateSignedXml(
                                                            WXPayUtil.objectToMap(wxPayRefundDTO), WX_PAY_KEY),
                                                    String.class)),
                                    WxPayRefundResultDTO.class);
            if (wxPayRefundResultDTO.getResult_code().equals("SUCCESS")
                    && wxPayRefundResultDTO.getReturn_code().equals("SUCCESS")) {
                TblBill tblBill = tblBillMapper.selectByPrimaryKey(tblBillChild.getBillId());
                // 充值单号
                tblBill.setRefundAmount(tblBill.getRefundAmount().add(billRealRefund));
                tblBillMapper.updateByPrimaryKeySelective(tblBill);
                tblBillChild.setRefundAmount(tblBillChild.getRefundAmount().add(billRealRefund));
                tblBillChildMapper.updateByPrimaryKey(tblBillChild);
                // 退款单号
                TblBillRefund tblBillRefund =
                        TblBillRefund.builder()
                                .id(refundId)
                                .amount(billRealRefund)
                                .billId(tblBillChild.getBillId())
                                .time(new Date())
                                .build();
                tblBillRefundMapper.insert(tblBillRefund);
                // 核销票数
                if (cancellationFlag) {
                    RLock lock = redissonSingle.getLock("gate-record-" + String.valueOf(tblRecord.getId()));
                    lock.lock(120, TimeUnit.SECONDS);
                    TblBillChild cancellation = new TblBillChild();
                    cancellation.setTicketPrice(tblBillChild.getTicketPrice().multiply(new BigDecimal("-1")));
                    cancellation.setTicketNum(
                            billRealRefund.divide(tblBillChild.getTicketPrice()).intValue());
                    updateRecord(tblRecord, cancellation);
                    lock.unlock();
                }
            }
        }
        // 如果是指定金额退款、全部核销至零
        if (!cancellationFlag) {
            cancellationAllTicket(tblRecord, shouldRefundAmount, true, reasonFlag);
        }
        if (totalRefundAmount.compareTo(new BigDecimal("0")) == 0) {
            return CommonResult.builder().status(200).msg("退款成功").build();
        } else {
            return CommonResult.builder().status(400).msg("部分退款成功,请联系管理员处理").build();
        }
    }

    @Override
    public CommonResult cancellation(Long recordId) {
        RLock lock = redissonSingle.getLock("gate-record-" + recordId);
        try {
            lock.lock(120, TimeUnit.SECONDS);
            TblRecord tblRecord = tblRecordMapper.selectByPrimaryKey(recordId);
            if (null == tblRecord) {
                return CommonResult.builder().status(10000).msg("无效票").build();
            }
            if (tblRecord.getAvailableNum() < 1) {
                return CommonResult.builder().status(400).msg("核销失败,票数不足").build();
            }
            // 不是退款引发的
            updateRecord(
                    tblRecord,
                    TblBillChild.builder().ticketPrice(new BigDecimal("-1000000000")).ticketNum(1).build());
            TblCheck tblCheck = new TblCheck();
            BeanUtils.copyProperties(tblRecord, tblCheck);
            tblCheck.setRecordId(tblRecord.getId());
            tblCheck.setTime(new Date());
            tblCheck.setTicketNum(1);
            tblCheck.setId(idBaseService.genId());
            tblCheckMapper.insert(tblCheck);
            return CommonResult.builder().status(200).msg(tblRecord.getTicketName()).build();
        } catch (Exception e) {
            log.warn("核销发生异常:" + recordId);
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return CommonResult.builder().status(400).msg("核销失败,票数不足").build();
    }

    @Override
    public CommonResult cancellationByCard(String phoneNum, Long parkId, Long productId, String id) {
        // 查出所有有票的行程
        CommonResult commonResult = record(phoneNum, 0, productId, parkId);
        if (commonResult.getStatus() == 400) {
            return commonResult;
        }
        List<TblRecord> tblRecords =
                JSONArray.parseArray(JSON.toJSONString(commonResult.getData()), TblRecord.class);
        List<TblRecord> tblRecords_ = new ArrayList<>();
        // 遍历
        for (TblRecord tblRecord : tblRecords) {
            // 传入胸卡方便后续记录
            RBucket<String> rBucket =
                    redissonSingle.getBucket("gate-check-" + String.valueOf(tblRecord.getId()));
            rBucket.set(id, 120, TimeUnit.SECONDS);
            // todo 其中一笔退款失败的问题
            if (tblRecord.getReturnableAmount().intValue() > 0) {
                // 如果是汉服，使用指定金额退还押金
                try {
                    specialRefund(
                            TblSpecialRefundDTO.builder()
                                    .managerId(1L)
                                    .phoneNum(tblRecord.getPhoneNum())
                                    .recordId(tblRecord.getId())
                                    .refundAmount(tblRecord.getReturnableAmount())
                                    .flag(false)
                                    .build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 核销所有的票,押金->0,不记录原因
                tblRecords_.add(cancellationAllTicket(tblRecord, new BigDecimal("0"), true, false));
            }
        }
        return CommonResult.builder().status(200).msg("核销成功").data(tblRecords_).build();
    }

    /**
     * 核销所有的票，不减少有效票数
     *
     * @param tblRecord            行程
     * @param shouldRefundAmount   应退金额
     * @param returnableAmountFlag 是否为指定金额退款
     * @param reasonFlag           汉服胸卡使用指定金额退款但是不做记录
     * @return 核销后的行程
     */
    private TblRecord cancellationAllTicket(
            TblRecord tblRecord,
            BigDecimal shouldRefundAmount,
            Boolean returnableAmountFlag,
            Boolean reasonFlag) {
        RLock lock = redissonSingle.getLock("gate-record-" + String.valueOf(tblRecord.getId()));
        lock.lock(120, TimeUnit.SECONDS);
        // 锁住之后再查询,返回自己真实核销的行程
        tblRecord = tblRecordMapper.selectByPrimaryKey(tblRecord.getId());
        RBucket<String> rBucket =
                redissonSingle.getBucket("gate-check-" + String.valueOf(tblRecord.getId()));
        String cardNo = rBucket.get() == null ? "" : rBucket.get();
        // 核销记录
        if (tblRecord.getAvailableNum() > 0) {
            tblCheckMapper.insert(
                    TblCheck.builder()
                            .time(new Date())
                            .phoneNum(tblRecord.getPhoneNum())
                            .id(idBaseService.genId())
                            .recordId(tblRecord.getId())
                            .ticketId(tblRecord.getTicketId())
                            .ticketNum(tblRecord.getAvailableNum())
                            .cardNo(cardNo)
                            .build());
        }
        // 票->0
        tblRecord.setUsedNum(tblRecord.getTotalNum());
        tblRecord.setAvailableNum(0);
        // 费用
        tblRecord.setRefundAmount(tblRecord.getRefundAmount().add(shouldRefundAmount));
        tblRecord.setIncome(tblRecord.getIncome().subtract(shouldRefundAmount));
        // 押金->0
        if (returnableAmountFlag) {
            tblRecord.setReturnableAmount(new BigDecimal("0"));
        }
        // 不是退押金需要记录原因
        if (reasonFlag) {
            tblRecord.setReason("指定金额退款");
        }
        tblRecordMapper.updateByPrimaryKey(tblRecord);
        lock.unlock();
        return tblRecord;
    }

    @Override
    public CommonResult selectCancellation(String phoneNum) {
        Example example = new Example(VCheck.class, true, true);
        example
                .createCriteria()
                .andEqualTo("phoneNum", phoneNum)
                .andLike("time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "%");
        List<VCheck> vChecks = vCheckMapper.selectByExample(example);
        return CommonResult.builder().status(200).msg("查询成功").data(vChecks).build();
    }

    @Override
    public CommonResult selectBills(String phoneNum) {
        Example example = new Example(TblBill.class, true, true);
        example
                .createCriteria()
                .andEqualTo("phoneNum", phoneNum)
                .andEqualTo("status", 1)
                .andLike("time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "%");
        List<TblBill> tblBills = tblBillMapper.selectByExample(example);
        return CommonResult.builder().status(200).msg("查询成功").data(tblBills).build();
    }

    private void updateRecord(TblRecord tblRecord, TblBillChild tblBillChild) {
        Integer availableNum = tblRecord.getAvailableNum();
        Integer totalNum = tblRecord.getTotalNum();
        Integer effectiveNum = tblRecord.getEffectiveNum();
        BigDecimal amount = tblRecord.getAmount();
        BigDecimal income = tblRecord.getIncome();
        BigDecimal returnableAmount = tblRecord.getReturnableAmount();
        if (tblBillChild.getTicketPrice().compareTo(new BigDecimal(0)) < 1) {
            // 核销
            availableNum -= tblBillChild.getTicketNum();
            // 如果是退款引发的核销需要减少收入
            if (tblBillChild.getTicketPrice().compareTo(new BigDecimal("-1000000000")) != 0) {
                income =
                        income.add(
                                tblBillChild
                                        .getTicketPrice()
                                        .multiply(new BigDecimal(tblBillChild.getTicketNum())));
                TblTicket tblTicket = tblTicketMapper.selectByPrimaryKey(tblRecord.getTicketId());
                returnableAmount =
                        returnableAmount.subtract(
                                tblTicket
                                        .getReturnableAmount()
                                        .multiply(new BigDecimal(tblBillChild.getTicketNum())));
            }
        } else {
            // 充值逻辑
            availableNum += tblBillChild.getTicketNum();
            effectiveNum += tblBillChild.getTicketNum();
            totalNum += tblBillChild.getTicketNum();
            BigDecimal total =
                    tblBillChild.getTicketPrice().multiply(new BigDecimal(tblBillChild.getTicketNum()));
            amount = amount.add(total);
            income = income.add(total);
            TblTicket tblTicket = tblTicketMapper.selectByPrimaryKey(tblRecord.getTicketId());
            returnableAmount =
                    returnableAmount.add(
                            tblTicket
                                    .getReturnableAmount()
                                    .multiply(new BigDecimal(tblBillChild.getTicketNum())));
        }
        Integer usedNum = totalNum - availableNum;
        BigDecimal refundAmount = amount.subtract(income);
        // 更新行程表
        tblRecord.setTotalNum(totalNum);
        tblRecord.setAvailableNum(availableNum);
        tblRecord.setUsedNum(usedNum);
        tblRecord.setAmount(amount);
        tblRecord.setRefundAmount(refundAmount);
        tblRecord.setIncome(income);
        tblRecord.setEffectiveNum(effectiveNum);
        tblRecord.setReturnableAmount(returnableAmount);
        tblRecordMapper.updateByPrimaryKey(tblRecord);
    }
}
