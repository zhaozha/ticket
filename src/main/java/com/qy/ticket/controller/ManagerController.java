package com.qy.ticket.controller;

import com.qy.ticket.annotation.IgnoreUserToken;
import com.qy.ticket.annotation.UserLock;
import com.qy.ticket.common.CommonResult;
import com.qy.ticket.dto.manager.*;
import com.qy.ticket.dto.user.TblRefundTrainDTO;
import com.qy.ticket.service.impl.ManagerServiceImpl;
import com.qy.ticket.service.impl.UserServiceImpl;
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
    private final ManagerServiceImpl managerService;
    private final UserServiceImpl userService;

    @IgnoreUserToken
    @GetMapping("/manager/openid/code/{code}")
    public CommonResult wxLogin(@PathVariable String code) throws Exception {
        return managerService.wxLogin(code);
    }

    @IgnoreUserToken
    @PostMapping("/manager/wx/register")
    public CommonResult wxRegister(@RequestBody RegisterDTO registerDTO) throws Exception {
        return managerService.wxRegister(registerDTO);
    }

    @IgnoreUserToken
    @GetMapping("/manager/ticket/parkId/{parkId}/productId/{productId}")
    public CommonResult selTicket(@PathVariable Long parkId, @PathVariable Long productId) throws Exception {
        return managerService.selTicket(parkId, productId);
    }

    @UserLock
    @PostMapping("/manager/ticket/price")
    public CommonResult updTicketPrice(@RequestBody TicketPriceDto ticketPriceDto) throws Exception {
        return managerService.updTicketPrice(ticketPriceDto);
    }

    @IgnoreUserToken
    @GetMapping("/manager/history/parkId/{parkId}/productId/{productId}/status/{status}")
    public CommonResult historyRecord(@PathVariable Long parkId, @PathVariable Long productId, @PathVariable Integer status) throws Exception {
        return managerService.historyRecord(status, productId, parkId);
    }

    @UserLock
    @PostMapping("/manager/ticket/cancellation")
    public CommonResult cancellation(@RequestBody CancellationDto cancellationDto) throws Exception {
        return managerService.cancellation(cancellationDto);
    }


    @IgnoreUserToken
    @PostMapping("/manager/add")
    public CommonResult addManager(@RequestBody AddManagerDTO addManagerDTO) {
        return managerService.addManager(addManagerDTO);
    }

    @IgnoreUserToken
    @PostMapping("/manager/delete")
    public CommonResult deleteManager(@RequestBody DeleteManagerDTO deleteManagerDTO) {
        return managerService.deleteManager(deleteManagerDTO);
    }

    @IgnoreUserToken
    @PostMapping("/manager/update")
    public CommonResult updateManager(@RequestBody AddManagerDTO addManagerDTO) {
        return managerService.updateManager(addManagerDTO);
    }

    @IgnoreUserToken
    @GetMapping("/manager/parkId/{parkId}/productId/{productId}/managerId/{managerId}/pageNum/{pageNum}/pageSize/{pageSize}")
    public CommonResult selectManager(
            @PathVariable Long parkId,
            @PathVariable Long productId,
            @PathVariable Long managerId,
            @PathVariable Integer pageNum,
            @PathVariable Integer pageSize) {
        return managerService.selectManager(parkId, productId, managerId, pageNum, pageSize);
    }

    @IgnoreUserToken
    @PostMapping("/manager/login")
    public CommonResult login(@RequestBody ManagerLoginDTO managerLoginDTO) {
        return managerService.login(managerLoginDTO);
    }

    @IgnoreUserToken
    @GetMapping("/detail/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}/pageNum/{pageNum}/pageSize/{pageSize}")
    public CommonResult selectBillByDetail(
            @PathVariable Long parkId,
            @PathVariable Long productId,
            @PathVariable String startTime,
            @PathVariable String endTime,
            @PathVariable Integer pageNum,
            @PathVariable Integer pageSize) {
        startTime = startTime + " 00:00:01";
        endTime = endTime + " 23:59:59";
        return managerService.selectBillByDetail(startTime, endTime, pageNum, pageSize, parkId, productId, true);
    }

    @IgnoreUserToken
    @GetMapping("/day/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}/pageNum/{pageNum}/pageSize/{pageSize}")
    public CommonResult selectBillByDay(
            @PathVariable Long parkId,
            @PathVariable Long productId,
            @PathVariable String startTime,
            @PathVariable String endTime,
            @PathVariable Integer pageNum,
            @PathVariable Integer pageSize) {
        startTime = startTime + " 00:00:01";
        endTime = endTime + " 23:59:59";
        return managerService.selectBillBySum(startTime, endTime, pageNum, pageSize, parkId, productId, 0, true);
    }

    @IgnoreUserToken
    @GetMapping("/month/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}/pageNum/{pageNum}/pageSize/{pageSize}")
    public CommonResult selectBillByMonth(
            @PathVariable Long parkId,
            @PathVariable Long productId,
            @PathVariable String startTime,
            @PathVariable String endTime,
            @PathVariable Integer pageNum,
            @PathVariable Integer pageSize) {
        startTime = startTime + "-01 00:00:01";
        endTime = endTime + "-31 23:59:59";
        return managerService.selectBillBySum(
                startTime, endTime, pageNum, pageSize, parkId, productId, 1, true);
    }

    @IgnoreUserToken
    @GetMapping("/pdf/day/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}")
    public void pdfDay(HttpServletResponse response,
                       @PathVariable Long parkId,
                       @PathVariable Long productId,
                       @PathVariable String startTime,
                       @PathVariable String endTime) {
        managerService.pdfDay(response, startTime, endTime, parkId, productId);
    }

    @IgnoreUserToken
    @GetMapping("/pdf/month/parkId/{parkId}/productId/{productId}/startTime/{startTime}/endTime/{endTime}")
    public void pdfMonth(HttpServletResponse response,
                         @PathVariable Long parkId,
                         @PathVariable Long productId,
                         @PathVariable String startTime,
                         @PathVariable String endTime) {
        managerService.pdfMonth(response, startTime, endTime, parkId, productId);
    }

    @UserLock
    @PostMapping("/manager/wx/refund")
    public CommonResult refund(@RequestBody TblRefundTrainDTO tblRefundTrainDTO) throws Exception {
        return userService.refundTrain(tblRefundTrainDTO.getRecordId(), tblRefundTrainDTO);
    }
}
