//package com.qy.ticket.controller;
//
//import com.qy.ticket.common.CommonResult;
//import com.qy.ticket.dao.TblCardMapper;
//import com.qy.ticket.dao.TblCheckMapper;
//import com.qy.ticket.dto.gate.*;
//import com.qy.ticket.entity.TblCard;
//import com.qy.ticket.entity.TblCheck;
//import com.qy.ticket.service.UserService;
//import com.virgo.virgoidgenerator.intf.IdBaseService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author zhaozha
// * @date 2020/1/8 下午1:54
// */
//@RestController
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@Slf4j
//public class GateController {
//  private final TblCardMapper tblCardMapper;
//  private final TblCheckMapper tblCheckMapper;
//
//  private final UserService userServiceImpl;
//
//  private final IdBaseService idBaseService;
//
//  @PostMapping("/gateapi/gatealive/")
//  public GateLiveResult gateLive(@RequestBody GateLiveDTO gateLiveDTO) {
//    return GateLiveResult.builder().status("200").errorMessage("").build();
//  }
//
//  @PostMapping("/gateapi/checkticket/")
//  public CheckTicketResult checkticket(@RequestBody CheckTicketDTO checkTicketDTO) {
//    // 员工卡逻辑
//    List<TblCard> tblCards = tblCardMapper.selectAll();
//    tblCards =
//        tblCards.stream()
//            .filter(s -> s.getTicketCode().equals(checkTicketDTO.getTicketCode()))
//            .collect(Collectors.toList());
//    CheckTicketResult checkTicketResult =
//        CheckTicketResult.builder()
//            .checkResult(0)
//            .checkType(0)
//            .VoiceNum(14)
//            .checkMsg1(" ")
//            .checkMsg2(" ")
//            .checkMsg3(" ")
//            .checkMsg4(" ")
//            .build();
//    if (!tblCards.isEmpty()) {
//      // 刷卡入库
//      String cardNo = tblCards.get(0).getCardNo();
//      tblCheckMapper.insert(
//          TblCheck.builder().id(idBaseService.genId()).time(new Date()).cardNo(cardNo).build());
//      checkTicketResult.setCheckResult(1);
//      checkTicketResult.setVoiceNum(5);
//    } else {
//      try {
//        CommonResult cancellation = userServiceImpl.cancellation(Long.parseLong(checkTicketDTO.getTicketCode()));
//        if (cancellation.getStatus() == 200) {
//          checkTicketResult.setCheckResult(1);
//          if(cancellation.getMsg().equals("成人票")){
//            checkTicketResult.setVoiceNum(1);
//          }else{
//            checkTicketResult.setVoiceNum(7);
//          }
//        }
//        if (cancellation.getStatus() == 400) {
//          checkTicketResult.setCheckResult(0);
//          checkTicketResult.setVoiceNum(35);
//        }
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//    return checkTicketResult;
//  }
//
//  @PostMapping("/gateapi/cancellation/")
//  public CancellationResult cancellation(@RequestBody CancellationDTO cancellationDTO) {
//    return CancellationResult.builder().checkResult("OK").build();
//  }
//}
