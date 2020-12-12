package com.qy.ticket.controller;

import com.qy.ticket.common.CommonResult;
import com.qy.ticket.dao.TblRecordMapper;
import com.qy.ticket.dto.manager.AddManagerDTO;
import com.qy.ticket.dto.manager.DeleteManagerDTO;
import com.qy.ticket.dto.manager.ManagerLoginDTO;
import com.qy.ticket.service.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zhaozha
 * @date 2020/1/10 下午6:10
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ManagerController {
  private final ManagerService managerServiceImpl;
  private final TblRecordMapper tblRecordMapper;

  @PostMapping("/manager/add")
  public CommonResult addManager(@RequestBody AddManagerDTO addManagerDTO) {
    return managerServiceImpl.addManager(addManagerDTO);
  }

  @PostMapping("/manager/delete")
  public CommonResult deleteManager(@RequestBody DeleteManagerDTO deleteManagerDTO) {
    return managerServiceImpl.deleteManager(deleteManagerDTO);
  }

  @PostMapping("/manager/update")
  public CommonResult updateManager(@RequestBody AddManagerDTO addManagerDTO) {
    return managerServiceImpl.updateManager(addManagerDTO);
  }

  @GetMapping(
      "/manager/parkId/{parkId}/productId/{productId}/managerId/{managerId}/pageNum/{pageNum}/pageSize/{pageSize}")
  public CommonResult selectManager(
      @PathVariable Long parkId,
      @PathVariable Long productId,
      @PathVariable Long managerId,
      @PathVariable Integer pageNum,
      @PathVariable Integer pageSize) {
    return managerServiceImpl.selectManager(parkId, productId, managerId, pageNum, pageSize);
  }

  @PostMapping("/manager/login")
  public CommonResult login(@RequestBody ManagerLoginDTO managerLoginDTO) {
    return managerServiceImpl.login(managerLoginDTO);
  }

  @GetMapping(
      "/detail/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}/pageNum/{pageNum}/pageSize/{pageSize}")
  public CommonResult selectBillByDetail(
      @PathVariable Long parkId,
      @PathVariable Long productId,
      @PathVariable String startTime,
      @PathVariable String endTime,
      @PathVariable Integer pageNum,
      @PathVariable Integer pageSize) {
    startTime = startTime + " 00:00:01";
    endTime = endTime + " 23:59:59";
    return managerServiceImpl.selectBillByDetail(
        startTime, endTime, pageNum, pageSize, parkId, productId, true);
  }

  @GetMapping(
      "/day/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}/pageNum/{pageNum}/pageSize/{pageSize}")
  public CommonResult selectBillByDay(
      @PathVariable Long parkId,
      @PathVariable Long productId,
      @PathVariable String startTime,
      @PathVariable String endTime,
      @PathVariable Integer pageNum,
      @PathVariable Integer pageSize) {
    startTime = startTime + " 00:00:01";
    endTime = endTime + " 23:59:59";
    return managerServiceImpl.selectBillBySum(
        startTime, endTime, pageNum, pageSize, parkId, productId, 0,true);
  }

  @GetMapping(
      "/month/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}/pageNum/{pageNum}/pageSize/{pageSize}")
  public CommonResult selectBillByMonth(
      @PathVariable Long parkId,
      @PathVariable Long productId,
      @PathVariable String startTime,
      @PathVariable String endTime,
      @PathVariable Integer pageNum,
      @PathVariable Integer pageSize) {
    startTime = startTime + "-01 00:00:01";
    endTime = endTime + "-31 23:59:59";
    return managerServiceImpl.selectBillBySum(
        startTime, endTime, pageNum, pageSize, parkId, productId, 1,true);
  }

  @GetMapping(
      "/pdf/day/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}")
  public void pdfDay(
      HttpServletResponse response,
      @PathVariable Long parkId,
      @PathVariable Long productId,
      @PathVariable String startTime,
      @PathVariable String endTime) {
    managerServiceImpl.pdfDay(response, startTime, endTime, parkId, productId);
  }

  @GetMapping(
          "/pdf/month/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}")
  public void pdfMonth(
          HttpServletResponse response,
          @PathVariable Long parkId,
          @PathVariable Long productId,
          @PathVariable String startTime,
          @PathVariable String endTime) {
    managerServiceImpl.pdfMonth(response, startTime, endTime, parkId, productId);
  }
}
