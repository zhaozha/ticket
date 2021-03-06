package com.qy.ticket.dto.gate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author zhaozha
 * @date 2020/1/8 下午2:23
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class GateDTO {
    private String gateNum;
    private String userName;
    private String password;
    private String reserved;
    private String InOutDirect;
    private String ticketType;
    private String ticketProperty;
    private String ticketCode;
}
