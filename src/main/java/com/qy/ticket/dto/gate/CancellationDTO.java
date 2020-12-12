package com.qy.ticket.dto.gate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author zhaozha
 * @date 2020/1/8 下午3:13
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CancellationDTO {
    private String InOutDirect;
    private String gateNum;
    private String ticketType;
    private String ticketProperty;
    private String ticketCode;
}
