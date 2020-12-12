package com.qy.ticket.dto.gate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author zhaozha
 * @date 2020/1/8 下午3:15
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CheckTicketResult {
    private Integer checkResult;
    private Integer checkType;
    private Integer VoiceNum;
    private Integer Recovery;
    private String checkMsg1;
    private String checkMsg2;
    private String checkMsg3;
    private String checkMsg4;
}
