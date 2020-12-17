package com.qy.ticket.controller;

import com.qy.ticket.annotation.IgnoreUserToken;
import com.qy.ticket.annotation.UserLock;
import com.qy.ticket.common.CommonResult;
import com.qy.ticket.dto.user.TblBillDTO;
import com.qy.ticket.dto.user.TblRefundDTO;
import com.qy.ticket.dto.user.TblSpecialRefundDTO;
import com.qy.ticket.dto.user.TblUserDto;
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

    @IgnoreUserToken
    @GetMapping("/openid/code/{code}")
    public CommonResult wxLogin(@PathVariable String code) throws Exception {
        return userService.wxLogin(code);
    }

    @IgnoreUserToken
    @PostMapping("/wx/register")
    public CommonResult wxRegister(@RequestBody TblUserDto tblUserDto) throws Exception {
        return userService.wxRegister(tblUserDto);
    }

    @IgnoreUserToken
    @GetMapping("/ticket/productId/{productId}/parkId/{parkId}")
    public CommonResult ticket(@PathVariable Long productId, @PathVariable Long parkId) throws Exception {
        return userService.ticket(productId, parkId);
    }

    @GetMapping("/record/phoneNum/{phoneNum}/status/{status}/productId/{productId}/parkId/{parkId}")
    public CommonResult record(@PathVariable String phoneNum, @PathVariable Integer status, @PathVariable Long productId, @PathVariable Long parkId) throws Exception {
        return userService.record(phoneNum, status, productId, parkId);
    }

    @GetMapping("/history/record/phoneNum/{phoneNum}/date/{date}/status/{status}/productId/{productId}/parkId/{parkId}")
    public CommonResult historyRecord(@PathVariable String phoneNum, @PathVariable String date, @PathVariable Integer status, @PathVariable Long productId, @PathVariable Long parkId) throws Exception {
        return userService.historyRecord(phoneNum, date, status, productId, parkId);
    }

    @UserLock
    @PostMapping("/wx/unifiedorder")
    public CommonResult unifiedorder(@RequestBody TblBillDTO tblBillDTO) throws Exception {
        return userService.unifiedorder(tblBillDTO);
    }

    @GetMapping("/bill/phoneNum/{phoneNum}")
    public CommonResult selectBills(@PathVariable String phoneNum) throws Exception {
        return userService.selectBills(phoneNum);
    }

    @PostMapping("/wx/refund")
    public CommonResult refund(@RequestBody TblRefundDTO tblRefundDTO) throws Exception {
        return userService.refund(tblRefundDTO.getRecordId(), tblRefundDTO);
    }

    @PostMapping("/wx/special/refund")
    public CommonResult specialRefund(@RequestBody TblSpecialRefundDTO tblSpecialRefundDTO) throws Exception {
        return userService.specialRefund(tblSpecialRefundDTO.getRecordId(), tblSpecialRefundDTO);
    }

    @GetMapping("/cancellation/phoneNum/{phoneNum}")
    public CommonResult selectCancellation(@PathVariable String phoneNum) throws Exception {
        return userService.selectCancellation(phoneNum);
    }

    @UserLock
    @GetMapping("/cancellation/card/phoneNum/{phoneNum}/parkId/{parkId}/productId/{productId}/id/{id}")
    public CommonResult cancellationByCard(@PathVariable String phoneNum, @PathVariable Long parkId, @PathVariable Long productId, @PathVariable String id) throws Exception {
        return userService.cancellationByCard(phoneNum, parkId, productId, id);
    }
}
