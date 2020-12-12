package com.qy.ticket.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qy.ticket.common.CommonResult;
import com.qy.ticket.dao.TblManagerMapper;
import com.qy.ticket.dao.TblRecordMapper;
import com.qy.ticket.dao.VTicketMapper;
import com.qy.ticket.dto.manager.*;
import com.qy.ticket.entity.TblManager;
import com.qy.ticket.entity.TblRecord;
import com.qy.ticket.entity.VTicket;
import com.qy.ticket.service.ManagerService;
import com.qy.ticket.util.DayPDFUtils;
import com.qy.ticket.util.MonthPDFUtils;
import com.virgo.virgoidgenerator.intf.IdBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  private final VTicketMapper vTicketMapper;

  private final IdBaseService idBaseService;

  @Override
  public CommonResult login(ManagerLoginDTO managerLoginDTO) {
    Example example = new Example(TblManager.class, true, true);
    example
        .createCriteria()
        .andEqualTo("phoneNum", managerLoginDTO.getPhoneNum())
        .andEqualTo("pwd", managerLoginDTO.getPwd());
    List<TblManager> tblManagers = tblManagerMapper.selectByExample(example);
    if (tblManagers.isEmpty()) {
      return CommonResult.builder().status(400).msg("管理员不存在").build();
    }
    TblManager tblManager = tblManagers.get(0);
    List<VTicket> vTickets = vTicketMapper.selectAll();
    List<LoginPowerDTO> list = distinctPower(vTickets);
    if (tblManager.getLevel() <= 2) {
      list =
          list.stream()
              .filter(
                  s ->
                      s.getParkId().longValue() == tblManager.getParkId().longValue()
                          && s.getProductId().longValue() == tblManager.getProductId().longValue())
              .collect(Collectors.toList());
    }
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("power", list);
    jsonObject.put("phoneNum", tblManager.getPhoneNum());
    jsonObject.put("id", tblManager.getId());
    jsonObject.put("level", tblManager.getLevel());
    return CommonResult.builder().status(200).msg("登陆成功").data(jsonObject).build();
  }

  public static List<LoginPowerDTO> distinctPower(List<VTicket> vTickets) {
    List<LoginPowerDTO> list = new ArrayList<>();
    Map<String, String> map = new HashMap<>();
    int count = map.size();
    for (int i = 0; i < vTickets.size(); i++) {
      VTicket vTicket = vTickets.get(i);
      LoginPowerDTO loginPowerDTO = new LoginPowerDTO();
      BeanUtils.copyProperties(vTicket, loginPowerDTO);
      map.put(vTicket.getProductId() + "" + vTicket.getParkId(), "1");
      if (map.size() > count) {
        list.add(loginPowerDTO);
        count = map.size();
      }
    }
    return list;
  }

  @Override
  public CommonResult addManager(AddManagerDTO addManagerDTO) {
    if (!power(addManagerDTO.getManagerId(), addManagerDTO.getLevel())) {
      return CommonResult.builder().status(400).msg("权限不足").build();
    }
    Example example = new Example(TblManager.class, true, true);
    example.createCriteria().andEqualTo("phoneNum", addManagerDTO.getPhoneNum());
    List<TblManager> tblManagers = tblManagerMapper.selectByExample(example);
    if (!tblManagers.isEmpty()) {
      return CommonResult.builder().status(400).msg("管理员已存在").build();
    }
    TblManager tblManager = new TblManager();
    BeanUtils.copyProperties(addManagerDTO, tblManager);
    tblManager.setId(idBaseService.genId());
    String phoneNum = tblManager.getPhoneNum();
    tblManager.setPwd(phoneNum.substring(phoneNum.length() - 4));
    tblManagerMapper.insert(tblManager);
    return CommonResult.builder().status(200).msg("添加成功").data(tblManager).build();
  }

  private Boolean power(Long managerId, Integer level) {
    TblManager tblManager = tblManagerMapper.selectByPrimaryKey(managerId);
    if (tblManager.getLevel() > level) {
      return true;
    }
    return false;
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
  public CommonResult updateManager(AddManagerDTO addManagerDTO) {
    if (!power(addManagerDTO.getManagerId(), addManagerDTO.getLevel())) {
      return CommonResult.builder().status(400).msg("权限不足").build();
    }
    Example example = new Example(TblManager.class, true, true);
    example.createCriteria().andEqualTo("phoneNum", addManagerDTO.getPhoneNum());
    List<TblManager> tblManagers = tblManagerMapper.selectByExample(example);
    if (tblManagers.isEmpty()) {
      return CommonResult.builder().status(400).msg("管理员不存在").build();
    }
    TblManager tblManager = tblManagers.get(0);
    BeanUtils.copyProperties(addManagerDTO, tblManager);
    String phoneNum = addManagerDTO.getPhoneNum();
    tblManager.setPwd(phoneNum.substring(phoneNum.length() - 4));
    tblManagerMapper.updateByPrimaryKey(tblManager);
    return CommonResult.builder().status(200).msg("更新成功").data(tblManager).build();
  }

  @Override
  public CommonResult selectManager(
      Long parkId, Long productId, Long managerId, Integer pageNum, Integer pageSize) {
    TblManager tblManager = tblManagerMapper.selectByPrimaryKey(managerId);
    if (null == tblManager) {
      return CommonResult.builder().status(400).msg("管理员不存在").build();
    }
    PageHelper.startPage(pageNum, pageSize);
    Example example = new Example(TblManager.class, true, true);
    example
        .createCriteria()
        .andLessThan("level", tblManager.getLevel())
        .andEqualTo("parkId", parkId)
        .andEqualTo("productId", productId);
    List<TblManager> tblManagers = tblManagerMapper.selectByExample(example);
    if (tblManagers.isEmpty()) {
      return CommonResult.builder().status(400).msg("无数据").build();
    }
    PageInfo<TblManager> pageInfo = new PageInfo<>(tblManagers);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("count", pageInfo.getTotal());
    jsonObject.put("data", tblManagers);
    return CommonResult.builder().status(200).msg("查询成功").data(jsonObject).build();
  }

  @Override
  public CommonResult selectBillByDetail(
      String startTime,
      String endTime,
      Integer pageNum,
      Integer pageSize,
      Long parkId,
      Long productId,
      Boolean pageFlag) {
    TblRecord sum = tblRecordMapper.Sum(startTime, endTime, parkId, productId);
    if (null == sum) {
      return CommonResult.builder().status(400).msg("无数据").build();
    }
    Example example = new Example(TblRecord.class, true, true);
    example.setOrderByClause("time DESC");
    example
        .createCriteria()
        .andEqualTo("parkId", parkId)
        .andEqualTo("productId", productId)
        .andGreaterThanOrEqualTo("time", startTime)
        .andLessThanOrEqualTo("time", endTime);
    if (pageFlag) {
      PageHelper.startPage(pageNum, pageSize);
    }
    List<TblRecord> tblRecords = tblRecordMapper.selectByExample(example);
    JSONObject jsonObject = new JSONObject();
    if (pageFlag) {
      PageInfo<TblRecord> pageInfo = new PageInfo<>(tblRecords);
      jsonObject.put("count", pageInfo.getTotal());
    }
    jsonObject.put("data", tblRecords);
    jsonObject.put("amount", sum.getAmount());
    jsonObject.put("refundAmount", sum.getRefundAmount());
    jsonObject.put("effectiveNum", sum.getEffectiveNum());
    jsonObject.put(
        "wxFee",
        sum.getIncome().multiply(new BigDecimal("0.006")).setScale(2, BigDecimal.ROUND_HALF_UP));
    jsonObject.put(
        "income",
        sum.getIncome().multiply(new BigDecimal("0.994")).setScale(2, BigDecimal.ROUND_HALF_UP));
    return CommonResult.builder().status(200).msg("查询成功").data(jsonObject).build();
  }

  @Override
  public CommonResult selectBillBySum(
      String startTime,
      String endTime,
      Integer pageNum,
      Integer pageSize,
      Long parkId,
      Long productId,
      Integer type,
      Boolean pageFlag) {
    TblRecord sum = tblRecordMapper.Sum(startTime, endTime, parkId, productId);
    if (null == sum) {
      return CommonResult.builder().status(400).msg("无数据").build();
    }
    if (pageFlag) {
      PageHelper.startPage(pageNum, pageSize);
    }
    List<TblRecord> tblRecords = new ArrayList<>();
    if (0 == type) {
      tblRecords = tblRecordMapper.Day(startTime, endTime, parkId, productId);
    } else {
      tblRecords = tblRecordMapper.Month(startTime, endTime, parkId, productId);
    }
    List<TblRecordDTO> list = new ArrayList<>();
    for (TblRecord tblRecord : tblRecords) {
      TblRecordDTO tblRecordDTO =
          TblRecordDTO.builder()
              .amount(tblRecord.getAmount())
              .effectiveNum(tblRecord.getEffectiveNum())
              .refundAmount(tblRecord.getRefundAmount())
              .income(
                  tblRecord
                      .getIncome()
                      .multiply(new BigDecimal("0.994"))
                      .setScale(2, BigDecimal.ROUND_HALF_UP))
              .wxFee(
                  tblRecord
                      .getIncome()
                      .multiply(new BigDecimal("0.006"))
                      .setScale(2, BigDecimal.ROUND_HALF_UP))
              .build();
      if (0 == type) {
        tblRecordDTO.setTime(new SimpleDateFormat("yyyy-MM-dd").format(tblRecord.getTime()));
      } else {
        tblRecordDTO.setTime(new SimpleDateFormat("yyyy-MM").format(tblRecord.getTime()));
      }
      list.add(tblRecordDTO);
    }
    JSONObject jsonObject = new JSONObject();
    if (pageFlag) {
      PageInfo<TblRecord> pageInfo = new PageInfo<>(tblRecords);
      jsonObject.put("count", pageInfo.getTotal());
    }
    jsonObject.put("data", list);
    jsonObject.put("amount", sum.getAmount());
    jsonObject.put("refundAmount", sum.getRefundAmount());
    jsonObject.put("effectiveNum", sum.getEffectiveNum());
    jsonObject.put(
        "wxFee",
        sum.getIncome().multiply(new BigDecimal("0.006")).setScale(2, BigDecimal.ROUND_HALF_UP));
    jsonObject.put(
        "income",
        sum.getIncome().multiply(new BigDecimal("0.994")).setScale(2, BigDecimal.ROUND_HALF_UP));
    return CommonResult.builder().status(200).msg("查询成功").data(jsonObject).build();
  }

  @Override
  public void pdfDay(
      HttpServletResponse response, String startTime, String endTime, Long parkId, Long productId) {
    try {
      String time = startTime;
      startTime = startTime + " 00:00:01";
      endTime = endTime + " 23:59:59";
      CommonResult commonResult =
          selectBillByDetail(startTime, endTime, 1, 10000, parkId, productId, false);
      JSONObject data = (JSONObject) commonResult.getData();
      List<TblRecord> tblRecords =
          JSONArray.parseArray(data.getJSONArray("data").toJSONString(), TblRecord.class);
      Example example = new Example(VTicket.class, true, true);
      example.createCriteria().andEqualTo("parkId", parkId).andEqualTo("productId", productId);
      List<VTicket> vTickets = vTicketMapper.selectByExample(example);
      VTicket vTicket = vTickets.get(0);
      String name = vTicket.getParkName() + vTicket.getProductName();
      DayPDFUtils.export(
          response,
          tblRecords,
          name,
          time,
          SumRecordDTO.builder()
              .income(new BigDecimal(data.getString("income")))
              .effectiveNum(data.getInteger("effectiveNum"))
              .wxFee(new BigDecimal(data.getString("wxFee")))
              .build());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void pdfMonth(
      HttpServletResponse response, String startTime, String endTime, Long parkId, Long productId) {
    try {
      String time = startTime;
      startTime = startTime + "-01 00:00:01";
      endTime = endTime + "-31 23:59:59";
      CommonResult commonResult =
          selectBillBySum(startTime, endTime, 1, 10000, parkId, productId, 1, false);
      JSONObject data = (JSONObject) commonResult.getData();
      List<TblRecordDTO> tblRecordDTOS =
          JSONArray.parseArray(data.getJSONArray("data").toJSONString(), TblRecordDTO.class);
      Example example = new Example(VTicket.class, true, true);
      example.createCriteria().andEqualTo("parkId", parkId).andEqualTo("productId", productId);
      List<VTicket> vTickets = vTicketMapper.selectByExample(example);
      VTicket vTicket = vTickets.get(0);
      MonthPDFUtils.export(
          vTicket.getParkName(),
          time,
          response,
          
          tblRecordDTOS,
          vTicket.getProportion(),
          SumRecordDTO.builder().income(new BigDecimal(data.getString("income"))).wxFee(new BigDecimal(data.getString("wxFee"))).build(),
          vTicket.getProductName());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
