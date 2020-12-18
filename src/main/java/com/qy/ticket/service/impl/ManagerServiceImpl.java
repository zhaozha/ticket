package com.qy.ticket.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qy.ticket.common.CommonResult;
import com.qy.ticket.constant.RedisConstant;
import com.qy.ticket.dao.*;
import com.qy.ticket.dto.manager.*;
import com.qy.ticket.entity.*;
import com.qy.ticket.service.ManagerService;
import com.qy.ticket.util.*;
import com.virgo.virgoidgenerator.intf.IdBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.qy.ticket.constant.SystemConstant.*;
import static com.qy.ticket.constant.SystemConstant.CONTEXT_KEY_USER_OPEN_ID;

/**
 * @author zhaozha
 * @date 2020/1/10 下午4:59
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ManagerServiceImpl implements ManagerService {
    private final TblManagerMapper tblManagerMapper;
    private final TblRecordMapper tblRecordMapper;
    private final TblRecordCustomizedMapper tblRecordCustomizedMapper;
    private final VTicketMapper vTicketMapper;
    private final TblTicketMapper tblTicketMapper;
    private final TblCheckCustomizedMapper tblCheckCustomizedMapper;

    private final IdBaseService idBaseService;
    private final RestTemplate restTemplate;
    private final RedissonClient redissonSingle;
    private final JwtUtil jwtOperator;

    @Value("${jwt.expire-time-in-second}")
    private Long expirationTimeInSecond;

    @Override
    public CommonResult wxLogin(String code) throws Exception {
        String url =
                "https://api.weixin.qq.com/sns/jscode2session?appid="
                        + "wx84bf27b5876fb8ec"
                        + "&secret=a58c04b5e209070ca40b1e2feefd8f0b"
                        + "&js_code="
                        + code
                        + "&grant_type=authorization_code";

        String result = restTemplate.getForObject(url, String.class);
        String openId = JSONObject.parseObject(result).getString("openid");
        String sessionKey = JSONObject.parseObject(result).getString("session_key");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", openId);

        List<TblManager> tblManagers = MapperUtil.getListByKVs(TblManager.class, tblManagerMapper, "openId", openId);
        if (CollectionUtils.isEmpty(tblManagers)) {
            jsonObject.put("phoneNum", "");
            jsonObject.put("id", "");
            jsonObject.put("sessionKey", sessionKey);
            return CommonResult.builder().status(400).msg("未注册").data(jsonObject).build();
        }

        TblManager tblManager = tblManagers.get(0);
        return CommonResult.builder().status(200).msg("查询成功").data(buildLoginRes(tblManager, sessionKey)).build();
    }

    @Override
    public CommonResult wxRegister(RegisterDTO registerDTO) throws Exception {
        String phoneNum = registerDTO.getPhoneNum();
        if (StringUtils.isEmpty(phoneNum)) {
            return CommonResult.builder().status(400).msg("手机号必须填写").data(null).build();
        }
        List<TblManager> tblManagers = MapperUtil.getListByKVs(TblManager.class, tblManagerMapper, "phoneNum", phoneNum);
        if (CollectionUtils.isEmpty(tblManagers)) {
            return CommonResult.builder().status(400).msg("管理员不存在").data(null).build();
        }
        TblManager tblManager = tblManagers.get(0);
        tblManager.setOpenId(registerDTO.getOpenId());
        tblManagerMapper.updateByPrimaryKeySelective(tblManager);
        return CommonResult.builder().status(200).msg("注册成功").data(buildLoginRes(tblManager, "")).build();
    }

    /**
     * 构建登录或者注册返回结构
     *
     * @param tblManager
     * @param sessionKey
     * @return
     */
    private JSONObject buildLoginRes(TblManager tblManager, String sessionKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", tblManager.getOpenId());
        jsonObject.put("phoneNum", tblManager.getPhoneNum());
        jsonObject.put("id", tblManager.getId());
        jsonObject.put("sessionKey", sessionKey);
        // token
        buildLoginToken(jsonObject, tblManager);
        // 权限
        List<VTicket> vTickets = vTicketMapper.selectAll();
        List<LoginPowerDTO> list = CollectionUtils.isEmpty(vTickets) ? new ArrayList<>() : JSONArray.parseArray(JSON.toJSONString(vTickets), LoginPowerDTO.class);
        if (!CollectionUtils.isEmpty(list)) {
            // 2级及以下给出范围
            if (tblManager.getLevel() <= 2) {
                list = list.stream().filter(s -> s.getParkId().equals(tblManager.getParkId()) && s.getProductId().equals(tblManager.getProductId())).collect(Collectors.toList());
            }
        }
        jsonObject.put("power", list);
        jsonObject.put("level", tblManager.getLevel());
        return jsonObject;
    }

    private void buildLoginToken(JSONObject jsonObject, TblManager tblManager) {
        RBucket<String> bucket = redissonSingle.getBucket(RedisConstant.concat(RedisConstant.KEY_USER_TOKEN, tblManager.getId().toString()));
        Map<String, Object> map = new HashMap<>();
        map.put(CONTEXT_KEY_USER_ID, tblManager.getId());
        map.put(CONTEXT_KEY_USER_NAME, tblManager.getNickName());
        map.put(CONTEXT_KEY_USER_PHONE, tblManager.getPhoneNum());
        map.put(CONTEXT_KEY_USER_OPEN_ID, tblManager.getOpenId());
        String token = jwtOperator.generateToken(map);
        bucket.set(token, expirationTimeInSecond, TimeUnit.SECONDS);
        jsonObject.put("token", token);
    }

    @Override
    public CommonResult login(ManagerLoginDTO managerLoginDTO) throws Exception {
        List<TblManager> tblManagers = MapperUtil.getListByKVs(TblManager.class, tblManagerMapper, "phoneNum", managerLoginDTO.getPhoneNum(), "pwd", managerLoginDTO.getPwd());
        if (CollectionUtils.isEmpty(tblManagers)) {
            return CommonResult.builder().status(400).msg("管理员不存在").build();
        }
        TblManager tblManager = tblManagers.get(0);
        return CommonResult.builder().status(200).msg("登陆成功").data(buildLoginRes(tblManager, "")).build();
    }


    @Override
    public CommonResult addManager(AddManagerDTO addManagerDTO) throws Exception {
        Long managerId = addManagerDTO.getManagerId();
        Integer level = addManagerDTO.getLevel();

        if (!power(managerId, level)) {
            return CommonResult.builder().status(400).msg("权限不足").build();
        }

        List<TblManager> tblManagers = MapperUtil.getListByKVs(TblManager.class, tblManagerMapper, "phoneNum", addManagerDTO.getPhoneNum());
        if (!CollectionUtils.isEmpty(tblManagers)) {
            return CommonResult.builder().status(400).msg("管理员已存在").build();
        }

        TblManager tblManager = new TblManager();
        BeanUtils.copyProperties(addManagerDTO, tblManager);
        tblManager.setId(idBaseService.genId());
        // 密码默认手机号后4位
        String phoneNum = tblManager.getPhoneNum();
        tblManager.setPwd(phoneNum.substring(phoneNum.length() - 4));
        tblManagerMapper.insert(tblManager);
        return CommonResult.builder().status(200).msg("添加成功").data(tblManager).build();
    }

    /**
     * 权限判断
     *
     * @param managerId
     * @param level
     * @return
     */
    private Boolean power(Long managerId, Integer level) {
        TblManager tblManager = tblManagerMapper.selectByPrimaryKey(managerId);
        return tblManager.getLevel() > level;
    }

    @Override
    public CommonResult deleteManager(DeleteManagerDTO deleteManagerDTO) {
        TblManager tblManager = tblManagerMapper.selectByPrimaryKey(deleteManagerDTO.getId());
        if (null == tblManager) {
            return CommonResult.builder().status(400).msg("管理员不存在").build();
        }
        if (!power(deleteManagerDTO.getManagerId(), tblManager.getLevel())) {
            return CommonResult.builder().status(400).msg("权限不足").build();
        }
        tblManagerMapper.deleteByPrimaryKey(tblManager.getId());
        return CommonResult.builder().status(200).msg("删除成功").build();
    }

    @Override
    public CommonResult updateManager(AddManagerDTO addManagerDTO) throws Exception {
        if (!power(addManagerDTO.getManagerId(), addManagerDTO.getLevel())) {
            return CommonResult.builder().status(400).msg("权限不足").build();
        }

        List<TblManager> tblManagers = MapperUtil.getListByKVs(TblManager.class, tblManagerMapper, "phoneNum", addManagerDTO.getPhoneNum());
        if (CollectionUtils.isEmpty(tblManagers)) {
            return CommonResult.builder().status(400).msg("管理员不存在").build();
        }

        TblManager tblManager = tblManagers.get(0);
        BeanUtils.copyProperties(addManagerDTO, tblManager);
        tblManagerMapper.updateByPrimaryKey(tblManager);

        return CommonResult.builder().status(200).msg("更新成功").data(tblManager).build();
    }

    @Override
    public CommonResult selectManager(Long parkId, Long productId, Long managerId, Integer pageNum, Integer pageSize) {
        TblManager tblManager = tblManagerMapper.selectByPrimaryKey(managerId);
        if (null == tblManager) {
            return CommonResult.builder().status(400).msg("管理员不存在").build();
        }

        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(TblManager.class, true, true);
        example.createCriteria()
                .andLessThan("level", tblManager.getLevel())
                .andEqualTo("parkId", parkId)
                .andEqualTo("productId", productId);
        List<TblManager> tblManagers = tblManagerMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(tblManagers)) {
            return CommonResult.builder().status(400).msg("无数据").build();
        }
        PageInfo<TblManager> pageInfo = new PageInfo<>(tblManagers);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", pageInfo.getTotal());
        jsonObject.put("data", tblManagers);
        return CommonResult.builder().status(200).msg("查询成功").data(jsonObject).build();
    }

    @Override
    public CommonResult selectBillByDetail(String startTime, String endTime, Integer pageNum, Integer pageSize, Long parkId, Long productId, Boolean pageFlag) {
        TblRecord sum = tblRecordCustomizedMapper.Sum(startTime, endTime, parkId, productId);
        if (null == sum) {
            return CommonResult.builder().status(400).msg("无数据").build();
        }
        Example example = new Example(TblRecord.class, true, true);
        example.setOrderByClause("time DESC");
        example.createCriteria()
                .andEqualTo("parkId", parkId)
                .andEqualTo("productId", productId)
                .andGreaterThanOrEqualTo("time", startTime)
                .andLessThanOrEqualTo("time", endTime);
        if (pageFlag) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<TblRecord> tblRecords = tblRecordMapper.selectByExample(example);
        SumDataDto sumDataDto = SumDataDto.builder()
                .data(tblRecords)
                .amount(sum.getAmount())
                .refundAmount(sum.getRefundAmount())
                .effectiveNum(sum.getEffectiveNum())
                .wxFee(NumberUtil.multiplyHalfUp(sum.getIncome(), 0.006))
                .income(NumberUtil.multiplyHalfUp(sum.getIncome(), 0.994))
                .build();
        if (pageFlag) {
            PageInfo<TblRecord> pageInfo = new PageInfo<>(tblRecords);
            sumDataDto.setCount(pageInfo.getTotal());
        }

        return CommonResult.builder().status(200).msg("查询成功").data(sumDataDto).build();
    }

    @Override
    public CommonResult selectBillBySum(String startTime, String endTime, Integer pageNum, Integer pageSize, Long parkId, Long productId, Integer type, Boolean pageFlag) {
        TblRecord sum = tblRecordCustomizedMapper.Sum(startTime, endTime, parkId, productId);
        if (null == sum) {
            return CommonResult.builder().status(400).msg("无数据").build();
        }
        if (pageFlag) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<TblRecord> tblRecords;
        if (0 == type) {
            tblRecords = tblRecordCustomizedMapper.Day(startTime, endTime, parkId, productId);
        } else {
            tblRecords = tblRecordCustomizedMapper.Month(startTime, endTime, parkId, productId);
        }
        List<TblRecordDTO> list = new ArrayList<>();
        for (TblRecord tblRecord : tblRecords) {
            TblRecordDTO tblRecordDTO = TblRecordDTO.builder()
                    .amount(tblRecord.getAmount())
                    .effectiveNum(tblRecord.getEffectiveNum())
                    .refundAmount(tblRecord.getRefundAmount())
                    .income(NumberUtil.multiplyHalfUp(tblRecord.getIncome(), 0.994))
                    .wxFee(NumberUtil.multiplyHalfUp(tblRecord.getIncome(), 0.006))
                    .build();
            if (0 == type) {
                tblRecordDTO.setTime(new SimpleDateFormat("yyyy-MM-dd").format(tblRecord.getTime()));
            } else {
                tblRecordDTO.setTime(new SimpleDateFormat("yyyy-MM").format(tblRecord.getTime()));
            }
            list.add(tblRecordDTO);
        }
        SumDataDto sumDataDto = SumDataDto.builder()
                .data(list)
                .amount(sum.getAmount())
                .refundAmount(sum.getRefundAmount())
                .effectiveNum(sum.getEffectiveNum())
                .wxFee(NumberUtil.multiplyHalfUp(sum.getIncome(), 0.006))
                .income(NumberUtil.multiplyHalfUp(sum.getIncome(), 0.994))
                .build();
        if (pageFlag) {
            PageInfo<TblRecord> pageInfo = new PageInfo<>(tblRecords);
            sumDataDto.setCount(pageInfo.getTotal());
        }

        return CommonResult.builder().status(200).msg("查询成功").data(sumDataDto).build();
    }

    @Override
    public void pdfDay(HttpServletResponse response, String startTime, String endTime, Long parkId, Long productId) {
        try {
            String time = startTime;
            startTime = startTime + " 00:00:01";
            endTime = endTime + " 23:59:59";
            CommonResult commonResult = selectBillByDetail(startTime, endTime, 1, 10000, parkId, productId, false);
            SumDataDto sumDataDto = JSONObject.parseObject(JSON.toJSONString(commonResult.getData()), SumDataDto.class);

            List<TblRecord> tblRecords = JSONArray.parseArray(JSON.toJSONString(sumDataDto.getData()), TblRecord.class);
            Example example = new Example(VTicket.class, true, true);
            example.createCriteria().andEqualTo("parkId", parkId).andEqualTo("productId", productId);
            List<VTicket> vTickets = vTicketMapper.selectByExample(example);

            VTicket vTicket = vTickets.get(0);
            String name = vTicket.getParkName() + vTicket.getProductName();

            DayPDFUtils.export(response, tblRecords, name, time,
                    SumRecordDTO.builder()
                            .effectiveNum(sumDataDto.getEffectiveNum())
                            .income(sumDataDto.getIncome())
                            .wxFee(sumDataDto.getWxFee())
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pdfMonth(HttpServletResponse response, String startTime, String endTime, Long parkId, Long productId) {
        try {
            String time = startTime;
            startTime = startTime + "-01 00:00:01";
            endTime = endTime + "-31 23:59:59";
            CommonResult commonResult =
                    selectBillBySum(startTime, endTime, 1, 10000, parkId, productId, 1, false);
            SumDataDto sumDataDto = JSONObject.parseObject(JSON.toJSONString(commonResult.getData()), SumDataDto.class);

            List<TblRecordDTO> tblRecordDTOS = JSONArray.parseArray(JSON.toJSONString(sumDataDto.getData()), TblRecordDTO.class);

            Example example = new Example(VTicket.class, true, true);
            example.createCriteria().andEqualTo("parkId", parkId).andEqualTo("productId", productId);
            List<VTicket> vTickets = vTicketMapper.selectByExample(example);
            VTicket vTicket = vTickets.get(0);

            MonthPDFUtils.export(vTicket.getParkName(), time, response, tblRecordDTOS, vTicket.getProportion(),
                    SumRecordDTO.builder()
                            .income(sumDataDto.getIncome())
                            .wxFee(sumDataDto.getWxFee())
                            .build(),
                    vTicket.getProductName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommonResult selTicket(Long parkId, Long productId) throws Exception {
        List<TblTicket> tblTickets = MapperUtil.getListByKVs(TblTicket.class, tblTicketMapper, "parkId", parkId, "productId", productId);
        return new CommonResult(200, "查询成功", tblTickets);
    }

    @Override
    public CommonResult updTicketPrice(List<TicketPriceDto> ticketPriceDtos) throws Exception {
        if (!CollectionUtils.isEmpty(ticketPriceDtos)) {
            for (TicketPriceDto ticketPriceDto : ticketPriceDtos) {
                if (null == ticketPriceDto.getPrice()) {
                    continue;
                }
                TblTicket tblTicket = new TblTicket();
                BeanUtils.copyProperties(ticketPriceDto, tblTicket);
                tblTicketMapper.updateByPrimaryKeySelective(tblTicket);
            }
        }
        return new CommonResult(200, "变更成功", null);
    }

    @Override
    public CommonResult historyRecord(Integer status, Long productId, Long parkId) {
        Example example = new Example(TblRecord.class, true, true);
        Example.Criteria criteria = example.createCriteria();
        if (0 == status) {
            criteria.andGreaterThan("availableNum", 0);
        } else {
            criteria.andEqualTo("availableNum", 0);
        }
        if (-1L != productId) {
            criteria.andEqualTo("productId", productId);
        }
        if (-1L != parkId) {
            criteria.andEqualTo("parkId", parkId);
        }
        criteria.andLike("time", DateUtil.yyyyMMdd.format(new Date()) + "%");
        List<TblRecord> tblRecords = tblRecordMapper.selectByExample(example);
        return new CommonResult(200, "变更成功", tblRecords);
    }

    @Override
    public CommonResult cancellation(CancellationDto cancellationDto) {
        tblRecordCustomizedMapper.cancellationAll2Upd(cancellationDto.getIds());
        dealCheckLog(cancellationDto);
        return new CommonResult(200, "核销成功", null);
    }

    /**
     * 核销记录
     *
     * @param
     */
    private void dealCheckLog(CancellationDto cancellationDto) {
        Example example = new Example(TblRecord.class, true, true);
        example.createCriteria().andIn("id", cancellationDto.getIds());
        List<TblRecord> tblRecords = tblRecordMapper.selectByExample(example);
        List<TblCheck> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tblRecords)) {
            for (TblRecord tblRecord : tblRecords) {
                TblCheck tblCheck = new TblCheck();
                BeanUtils.copyProperties(tblRecord, tblCheck);
                tblCheck.setRecordId(tblRecord.getId());
                tblCheck.setTime(new Date());
                tblCheck.setTicketNum(tblRecord.getEffectiveNum());
                tblCheck.setId(idBaseService.genId());
                tblCheck.setCardNo("1");
                list.add(tblCheck);
            }
        }
        tblCheckCustomizedMapper.insertList(list);
    }
}
