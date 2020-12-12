package com.qy.ticket.controller;

import com.qy.ticket.common.CommonResult;
import com.qy.ticket.dto.user.TblBillDTO;
import com.qy.ticket.dto.user.TblRefundDTO;
import com.qy.ticket.dto.user.TblSpecialRefundDTO;
import com.qy.ticket.entity.TblUser;
import com.qy.ticket.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaozha
 * @date 2020/1/6 下午3:05
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
  private final UserServiceImpl userService;

  @GetMapping("/openid/code/{code}")
  public CommonResult wxLogin(@PathVariable String code) {
    return userService.wxLogin(code);
  }

  @PostMapping("/wx/register")
  public CommonResult wxRegister(@RequestBody TblUser tblUser) {
    return userService.wxRegister(tblUser);
  }

  @PostMapping("/wx/unifiedorder")
  public CommonResult unifiedorder(@RequestBody TblBillDTO tblBillDTO) {
    try {
      return userService.unifiedorder(tblBillDTO);
    } catch (Exception e) {
      e.printStackTrace();
      return CommonResult.builder().status(500).msg("系统异常").build();
    }
  }

  @PostMapping("/wx/refund")
  public CommonResult refund(@RequestBody TblRefundDTO tblRefundDTO) {
    try {
      return userService.refund(tblRefundDTO);
    } catch (Exception e) {
      e.printStackTrace();
      return CommonResult.builder().status(500).msg("系统异常").build();
    }
  }

  @PostMapping("/wx/special/refund")
  public CommonResult specialRefund(@RequestBody TblSpecialRefundDTO tblSpecialRefundDTO) {
    try {
      return userService.specialRefund(tblSpecialRefundDTO);
    } catch (Exception e) {
      e.printStackTrace();
      return CommonResult.builder().status(500).msg("系统异常").build();
    }
  }

  @GetMapping("/ticket/productId/{productId}/parkId/{parkId}")
  public CommonResult ticket(@PathVariable Long productId, @PathVariable Long parkId) {
    return userService.ticket(productId, parkId);
  }

  @GetMapping("/record/phoneNum/{phoneNum}/status/{status}/productId/{productId}/parkId/{parkId}")
  public CommonResult record(
      @PathVariable String phoneNum,
      @PathVariable Integer status,
      @PathVariable Long productId,
      @PathVariable Long parkId) {
    return userService.record(phoneNum, status, productId, parkId);
  }

  @GetMapping(
      "/history/record/phoneNum/{phoneNum}/date/{date}/status/{status}/productId/{productId}/parkId/{parkId}")
  public CommonResult historyRecord(
      @PathVariable String phoneNum,
      @PathVariable String date,
      @PathVariable Integer status,
      @PathVariable Long productId,
      @PathVariable Long parkId) {
    return userService.historyRecord(phoneNum, date, status, productId, parkId);
  }

  @GetMapping("/cancellation/phoneNum/{phoneNum}")
  public CommonResult selectCancellation(@PathVariable String phoneNum) {
    return userService.selectCancellation(phoneNum);
  }

  @GetMapping("/bill/phoneNum/{phoneNum}")
  public CommonResult selectBills(@PathVariable String phoneNum) {
    return userService.selectBills(phoneNum);
  }

  @GetMapping(
      "/cancellation/card/phoneNum/{phoneNum}/parkId/{parkId}/productId/{productId}/id/{id}")
  public CommonResult cancellationByCard(
      @PathVariable String phoneNum,
      @PathVariable Long parkId,
      @PathVariable Long productId,
      @PathVariable String id) {
    return userService.cancellationByCard(phoneNum, parkId, productId, id);
  }
}
