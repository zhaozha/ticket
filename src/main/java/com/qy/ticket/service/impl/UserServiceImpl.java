package com.qy.ticket.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qy.ticket.annotation.RecordLock;
import com.qy.ticket.common.CommonResult;
import com.qy.ticket.constant.RedisConstant;
import com.qy.ticket.dao.*;
import com.qy.ticket.dto.user.TblBillDTO;
import com.qy.ticket.dto.user.TblRefundDTO;
import com.qy.ticket.dto.user.TblSpecialRefundDTO;
import com.qy.ticket.dto.user.TblTicketDTO;
import com.qy.ticket.dto.wx.*;
import com.qy.ticket.entity.*;
import com.qy.ticket.service.UserService;
import com.qy.ticket.util.*;
import com.virgo.virgoidgenerator.intf.IdBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.qy.ticket.constant.RedisConstant.KEY_TICKET_SEQ;
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
    private final TblBillCustomizedMapper tblBillCustomizedMapper;
    private final TblBillChildCustomizedMapper tblBillChildCustomizedMapper;
    private final TblRecordCustomizedMapper tblRecordCustomizedMapper;

    private final IdBaseService idBaseService;
    private final RestTemplate restTemplate;
    private final RestTemplate wxRefundRestTemplate;
    private final RedissonClient redissonSingle;
    private final JwtUtil jwtOperator;

    @Override
    public CommonResult wxLogin(String code) throws Exception {
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
        return CommonResult.builder().status(200).msg("查询成功").data(buildLoginRes(tblUser, sessionKey)).build();
    }

    @Override
    public CommonResult wxRegister(TblUser tblUser) throws Exception {
        String phoneNum = tblUser.getPhoneNum();
        if (StringUtils.isEmpty(phoneNum)) {
            return CommonResult.builder().status(400).msg("手机号必须填写").data(tblUser).build();
        }
        tblUser.setId(idBaseService.genId());
        tblUserMapper.insert(tblUser);
        return CommonResult.builder().status(200).msg("注册成功").data(buildLoginRes(tblUser, "")).build();
    }

    /**
     * 构建登录或者注册返回结构
     *
     * @param tblUser
     * @param sessionKey
     * @return
     */
    private JSONObject buildLoginRes(TblUser tblUser, String sessionKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", tblUser.getOpenId());
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
        return jsonObject;
    }


    @Override
    @Transactional
    public CommonResult unifiedorder(TblBillDTO tblBillDTO) throws Exception {
        Long billId = idBaseService.genId();
        // 统一下单
        WxUnifiedorderDTO wxUnifiedorderDTO = WxUnifiedorderDTO.builder()
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
        String retStr = restTemplate.postForObject(DOMAIN_API + UNIFIEDORDER_URL_SUFFIX, WXPayUtil.generateSignedXml(WXPayUtil.objectToMap(wxUnifiedorderDTO), WX_PAY_KEY), String.class);
        WxUnifiedorderResultDTO wxUnifiedorderResultDTO = (WxUnifiedorderResultDTO) WXPayUtil.mapToObject(WXPayUtil.xmlToMap(retStr), WxUnifiedorderResultDTO.class);
        // 业务逻辑
        if (wxUnifiedorderResultDTO.getResult_code().equals("SUCCESS") && wxUnifiedorderResultDTO.getReturn_code().equals("SUCCESS")) {
            WxPayResultDTO wxPayResultDTO = WxPayResultDTO.builder()
                    .appId(WX_APP_ID)
                    .nonceStr(WXPayUtil.generateNonceStr())
                    .wxPackage("prepay_id=" + wxUnifiedorderResultDTO.getPrepay_id())
                    .signType("MD5")
                    .timeStamp(String.valueOf(WXPayUtil.getCurrentTimestampMs()))
                    .build();
            wxPayResultDTO.setPaySign(WXPayUtil.xmlToMap(WXPayUtil.generateSignedXml(WXPayUtil.objectToMap(wxPayResultDTO), WX_PAY_KEY)).get("sign"));
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
            tblBill.setRefundAmount(0);
            tblBillMapper.insert(tblBill);

            // 详细清单 & 校验
            int amount = tblBill.getAmount();
            for (TblTicketDTO tblTicketDTO : list) {
                Long ticketId = tblTicketDTO.getTicketId();
                Integer ticketNum = tblTicketDTO.getTicketNum();

                TblTicket ticket = tblTicketMapper.selectByPrimaryKey(ticketId);
                Integer price = ticket.getPrice();
                Integer returnableAmount = ticket.getReturnableAmount();
                int total = price * tblTicketDTO.getTicketNum();

                TblBillChild tblBillChild = new TblBillChild();
                BeanUtils.copyProperties(tblBill, tblBillChild);
                BeanUtils.copyProperties(tblTicketDTO, tblBillChild);
                tblBillChild.setAmount(total);
                tblBillChild.setTicketPrice(price);
                tblBillChild.setId(idBaseService.genId());
                tblBillChild.setBillId(tblBill.getId());
                tblBillChild.setFatherAmount(tblBill.getAmount());
                tblBillChild.setReturnableAmount(returnableAmount * ticketNum);
                tblBillChildMapper.insert(tblBillChild);
                // 防止价格下单后变更
                amount -= total;
            }
            if (amount != 0) {
                return CommonResult.builder().status(400).msg("支付参数有误").build();
            }
            return CommonResult.builder().status(200).msg("下单成功").data(wxPayResultDTO).build();
        }
        return CommonResult.builder().status(400).msg("下单失败,请您重试").build();
    }

    @Override
    @Transactional
    public String wxPayConfirm(String xmlStr) throws Exception {
        WxPayConformDTO wxPayConform = (WxPayConformDTO) WXPayUtil.mapToObject(WXPayUtil.xmlToMap(xmlStr), WxPayConformDTO.class);
        if (wxPayConform.getResult_code().equals("SUCCESS") && wxPayConform.getReturn_code().equals("SUCCESS")) {
            long billId = Long.parseLong(wxPayConform.getOut_trade_no());
            // 处理账单&接口幂等
            int i = tblBillCustomizedMapper.change2PayStatus(billId);
            if (i == 0) {
                return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            }

            TblBill tblBill = tblBillMapper.selectByPrimaryKey(billId);
            String phoneNum = tblBill.getPhoneNum();

            Example example = new Example(TblRecord.class, true, true);
            example.createCriteria()
                    .andLike("time", DateUtil.yyyyMMdd.format(new Date()) + "%")
                    .andEqualTo("phoneNum", phoneNum);
            List<TblRecord> tblRecords = tblRecordMapper.selectByExample(example);

            List<TblBillChild> tblBillChildren = MapperUtil.getListByKVs(TblBillChild.class, tblBillChildMapper, "billId", billId);
            for (TblBillChild tblBillChild : tblBillChildren) {
                // 子订单幂等
                int j = tblBillChildCustomizedMapper.change2PayStatus(tblBillChild.getId());
                if (j == 0) {
                    continue;
                }
                if (CollectionUtils.isEmpty(tblRecords)) {
                    fistChargeBusiness(tblBillChild);
                } else {
                    List<TblRecord> collect = tblRecords.stream().filter(s -> s.getTicketId().equals(tblBillChild.getTicketId())).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(collect)) {
                        fistChargeBusiness(tblBillChild);
                    } else {
                        secondChargeBusiness(collect.get(0), tblBillChild);
                    }
                }
            }
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        }
        return null;
    }

    /**
     * 首充逻辑
     *
     * @param tblBillChild
     */
    private void fistChargeBusiness(TblBillChild tblBillChild) throws Exception {
        // 价格以下单时的价格为准
        long recordId = idBaseService.genId();
        Integer ticketPrice = tblBillChild.getTicketPrice();
        Integer returnableAmount = tblBillChild.getReturnableAmount();
        Integer ticketNum = tblBillChild.getTicketNum();
        Long productId = tblBillChild.getProductId();
        Long parkId = tblBillChild.getParkId();
        Integer payAmount = ticketPrice * ticketNum;
        // 景区+产品+时间构成票号步长的key
        RAtomicLong rAtomicLong = redissonSingle.getAtomicLong(RedisConstant.concat(KEY_TICKET_SEQ, parkId.toString(), productId.toString(), DateUtil.yyyyMMdd.format(new Date())));
        long seq = rAtomicLong.incrementAndGet();

        VTicket vTicket = MapperUtil.getOneByKVs(VTicket.class, vTicketMapper, null, "ticketId", tblBillChild.getTicketId());
        TblRecord tblRecord = new TblRecord();
        BeanUtils.copyProperties(vTicket, tblRecord);

        tblRecord.setId(recordId);
        tblRecord.setTime(new Date());
        tblRecord.setPhoneNum(tblBillChild.getPhoneNum());

        tblRecord.setIncome(payAmount);
        tblRecord.setAmount(payAmount);
        tblRecord.setRefundAmount(0);

        tblRecord.setAvailableNum(ticketNum);
        tblRecord.setUsedNum(0);
        tblRecord.setTotalNum(ticketNum);

        tblRecord.setUserId(tblBillChild.getUserId());
        tblRecord.setVersionId(1);

        tblRecord.setTicketPrice(ticketPrice);
        // 行程累计可退
        tblRecord.setReturnableAmount(returnableAmount);
        tblRecord.setEffectiveNum(ticketNum);

        tblRecord.setReason("");
        tblRecord.setSeq(String.format("%03d", seq));

        tblRecordMapper.insert(tblRecord);
    }

    /**
     * 非首冲逻辑
     *
     * @param tblRecord
     * @param tblBillChild
     */
    private void secondChargeBusiness(TblRecord tblRecord, TblBillChild tblBillChild) {
        Long id = tblRecord.getId();
        Integer amount = tblBillChild.getAmount();
        Integer returnableAmount = tblBillChild.getReturnableAmount();
        Integer ticketNum = tblBillChild.getTicketNum();
        tblRecordCustomizedMapper.charge2Upd(id, amount, returnableAmount, ticketNum);
    }


    @Override
    public CommonResult ticket(Long productId, Long parkId) throws Exception {
        List<VTicket> vTickets = MapperUtil.getListByKVs(VTicket.class, vTicketMapper, "productId", productId, "parkId", parkId);
        return CommonResult.builder().status(200).msg("查询成功").data(vTickets).build();
    }

    @Override
    public CommonResult record(String phoneNum, Integer status, Long productId, Long parkId) throws Exception {
        return historyRecord(phoneNum, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), status, productId, parkId);
    }

    @Override
    public CommonResult historyRecord(String phoneNum, String date, Integer status, Long productId, Long parkId) {
        Example example = new Example(TblRecord.class, true, true);
        Example.Criteria criteria = example.createCriteria();
        if (0 == status) {
            criteria.andGreaterThan("availableNum", 0);
        }
        if (-1L != productId) {
            criteria.andEqualTo("productId", productId);
        }
        if (-1L != parkId) {
            criteria.andEqualTo("parkId", parkId);
        }
        criteria.andLike("time", date + "%").andEqualTo("phoneNum", phoneNum);
        List<TblRecord> tblRecords = tblRecordMapper.selectByExample(example);
        if (tblRecords.isEmpty()) {
            return CommonResult.builder().status(400).msg("无数据").build();
        }
        return CommonResult.builder().status(200).msg("查询成功").data(tblRecords).build();
    }

    @RecordLock
    @Override
    @Transactional
    public CommonResult refund(Long recordId, TblRefundDTO tblRefundDTO) throws Exception {
        // 票数合规检测
        Integer ticketNum = tblRefundDTO.getTicketNum();
        TblRecord tblRecord = tblRecordMapper.selectByPrimaryKey(recordId);
        if (tblRecord.getAvailableNum() < ticketNum) {
            return CommonResult.builder().status(400).msg("超过可退票数").build();
        }
        // 退款金额计算
        Integer ticketPrice = tblRecord.getTicketPrice();
        Integer income = tblRecord.getIncome();
        Integer totalRefundAmount = ticketPrice * ticketNum;

        if (income - totalRefundAmount < 0) {
            return CommonResult.builder().status(400).msg("退款金额有误").build();
        }

        CommonResult commonResult = circleRefund(tblRecord, totalRefundAmount);
        // 退款成功修改有效票数
        if (commonResult.getStatus() == 200) {
            tblRecordCustomizedMapper.refund2Upd(recordId, ticketNum);
        }
        return commonResult;
    }

    @RecordLock
    @Override
    @Transactional
    public CommonResult specialRefund(Long recordId, TblSpecialRefundDTO tblSpecialRefundDTO) throws Exception {
        int refundAmount = tblSpecialRefundDTO.getRefundAmount();
        TblRecord tblRecord = tblRecordMapper.selectByPrimaryKey(recordId);
        if (null == tblRecord) {
            return CommonResult.builder().status(400).msg("票信息有误").build();
        }
        if (tblRecord.getIncome() < refundAmount) {
            return CommonResult.builder().status(400).msg("退款金额有误").build();
        }
        // 如果是退押金引发的指定金额退款、不记录原因
        CommonResult commonResult = circleRefund(tblRecord, refundAmount);
        // 核销所有票
        if (commonResult.getStatus() == 200) {
            tblRecordCustomizedMapper.cancellation2Upd(recordId);
        }
        return commonResult;
    }

    /**
     * 循环退款
     *
     * @param tblRecord
     * @param totalRefundAmount
     * @return
     * @throws Exception
     */
    private CommonResult circleRefund(TblRecord tblRecord, Integer totalRefundAmount) throws Exception {
        List<TblBillChild> tblBillChildren = MapperUtil.getListByKVs(TblBillChild.class, tblBillChildMapper, "recordId", tblRecord.getId(), "status", 1);
        if (!CollectionUtils.isEmpty(tblBillChildren)) {
            Map<Long, List<TblBillChild>> map = tblBillChildren.stream().collect(Collectors.groupingBy(TblBillChild::getBillId));
            if (!CollectionUtils.isEmpty(map)) {
                for (Long billId : map.keySet()) {
                    List<TblBillChild> childList = map.get(billId);
                    int totalRefund = 0;
                    for (TblBillChild tblBillChild : childList) {
                        int billCanRefund = tblBillChild.getAmount() - tblBillChild.getRefundAmount();
                        if (billCanRefund <= 0) {
                            continue;
                        }
                        // 得到本单退款金额
                        int billRealRefund;
                        if (billCanRefund <= totalRefundAmount) {
                            totalRefundAmount -= billCanRefund;
                            billRealRefund = billCanRefund;
                        } else {
                            totalRefundAmount = 0;
                            billRealRefund = totalRefundAmount;
                        }
                        totalRefund += billRealRefund;
                        // 账单变更
                        tblBillCustomizedMapper.refund2Upd(tblBillChild.getBillId(), billRealRefund);
                        tblBillChildCustomizedMapper.refund2Upd(tblBillChild.getId(), billRealRefund);
                    }
                    refund(billId, childList.get(0).getFatherAmount(), totalRefund);
                }
            }
        }
        if (totalRefundAmount == 0) {
            return CommonResult.builder().status(200).msg("退款成功").build();
        } else {
            return CommonResult.builder().status(400).msg("部分退款成功,请联系管理员处理").build();
        }
    }

    /**
     * 微信退款
     *
     * @param billId
     * @param totalFee
     * @param totalRefund
     * @throws Exception
     */
    private void refund(Long billId, Integer totalFee, Integer totalRefund) throws Exception {
        long refundId = idBaseService.genId();
        WxPayRefundDTO wxPayRefundDTO = WxPayRefundDTO.builder()
                .mch_id(WX_PAY_MCH)
                .appid(WX_APP_ID)
                .nonce_str(WXPayUtil.generateNonceStr())
                .out_trade_no(String.valueOf(billId))
                .out_refund_no(String.valueOf(refundId))
                .total_fee(totalFee)
                .build();
        wxPayRefundDTO.setRefund_fee(totalRefund);

        WxPayRefundResultDTO wxPayRefundResultDTO = (WxPayRefundResultDTO) WXPayUtil.mapToObject(WXPayUtil.xmlToMap(wxRefundRestTemplate.postForObject(DOMAIN_API + REFUND_URL_SUFFIX, WXPayUtil.generateSignedXml(WXPayUtil.objectToMap(wxPayRefundDTO), WX_PAY_KEY), String.class)), WxPayRefundResultDTO.class);
        if (wxPayRefundResultDTO.getResult_code().equals("SUCCESS") && wxPayRefundResultDTO.getReturn_code().equals("SUCCESS")) {
            TblBillRefund tblBillRefund = TblBillRefund.builder().id(refundId).amount(totalRefund).billId(billId).time(new Date()).build();
            tblBillRefundMapper.insert(tblBillRefund);
        } else {
            throw new RuntimeException("退款发生异常");
        }
    }

    @Transactional
    @Override
    public CommonResult cancellation(Long recordId) throws Exception {
        TblRecord tblRecord = tblRecordMapper.selectByPrimaryKey(recordId);
        if (null == tblRecord) {
            return CommonResult.builder().status(10000).msg("无效票").build();
        }
        if (tblRecord.getAvailableNum() < 1) {
            return CommonResult.builder().status(400).msg("核销失败,票数不足").build();
        }
        tblRecordCustomizedMapper.cancellation2Upd(recordId);
        dealCheckLog(tblRecord);
        return CommonResult.builder().status(200).msg("核销成功").build();
    }

    @Transactional
    @Override
    public CommonResult cancellationByCard(String phoneNum, Long parkId, Long productId, String id) throws Exception {
        // 查出所有有票的行程
        CommonResult commonResult = record(phoneNum, 0, productId, parkId);
        if (commonResult.getStatus() == 400) {
            return commonResult;
        }
        List<TblRecord> tblRecords = JSONArray.parseArray(JSON.toJSONString(commonResult.getData()), TblRecord.class);
        if (!CollectionUtils.isEmpty(tblRecords)) {
            List<Long> collect = tblRecords.stream().map(TblRecord::getId).collect(Collectors.toList());
            tblRecordCustomizedMapper.cancellationAll2Upd(collect);
            tblRecords.forEach(this::dealCheckLog);
            // 退款 todo
        }
        return CommonResult.builder().status(200).msg("核销成功").data(null).build();
    }

    /**
     * 核销记录
     *
     * @param tblRecord
     */
    private void dealCheckLog(TblRecord tblRecord) {
        TblCheck tblCheck = new TblCheck();
        BeanUtils.copyProperties(tblRecord, tblCheck);
        tblCheck.setRecordId(tblRecord.getId());
        tblCheck.setTime(new Date());
        tblCheck.setTicketNum(tblRecord.getEffectiveNum());
        tblCheck.setId(idBaseService.genId());
        tblCheckMapper.insert(tblCheck);
    }

    @Override
    public CommonResult selectCancellation(String phoneNum) {
        Example example = new Example(VCheck.class, true, true);
        example.createCriteria()
                .andEqualTo("phoneNum", phoneNum)
                .andLike("time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "%");
        List<VCheck> vChecks = vCheckMapper.selectByExample(example);
        return CommonResult.builder().status(200).msg("查询成功").data(vChecks).build();
    }

    @Override
    public CommonResult selectBills(String phoneNum) {
        Example example = new Example(TblBill.class, true, true);
        example.createCriteria()
                .andEqualTo("phoneNum", phoneNum)
                .andEqualTo("status", 1)
                .andLike("time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "%");
        List<TblBill> tblBills = tblBillMapper.selectByExample(example);
        return CommonResult.builder().status(200).msg("查询成功").data(tblBills).build();
    }

}
