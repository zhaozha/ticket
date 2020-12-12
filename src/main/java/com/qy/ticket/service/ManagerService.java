package com.qy.ticket.service;

import com.qy.ticket.common.CommonResult;
import com.qy.ticket.dto.manager.AddManagerDTO;
import com.qy.ticket.dto.manager.DeleteManagerDTO;
import com.qy.ticket.dto.manager.ManagerLoginDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zhaozha
 * @date 2020/1/10 下午4:59
 */
public interface ManagerService {
  CommonResult login(ManagerLoginDTO managerLoginDTO);

  CommonResult addManager(AddManagerDTO addManagerDTO);

  CommonResult deleteManager(DeleteManagerDTO deleteManagerDTO);

  CommonResult updateManager(AddManagerDTO addManagerDTO);

  CommonResult selectManager(
      Long parkId, Long productId, Long managerId, Integer pageNum, Integer pageSize);

  CommonResult selectBillByDetail(
      String startTime,
      String endTime,
      Integer pageNum,
      Integer pageSize,
      Long parkId,
      Long productId,
      Boolean pageFlag);

  CommonResult selectBillBySum(
      String startTime,
      String endTime,
      Integer pageNum,
      Integer pageSize,
      Long parkId,
      Long productId,
      Integer type,
      Boolean pageFlag);

  void pdfDay(
      HttpServletResponse response, String startTime, String endTime, Long parkId, Long productId);

  void pdfMonth(
      HttpServletResponse response, String startTime, String endTime, Long parkId, Long productId);
}