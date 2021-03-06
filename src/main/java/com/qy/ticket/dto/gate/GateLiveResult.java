package com.qy.ticket.dto.gate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author zhaozha
 * @date 2020/1/8 下午3:17
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class GateLiveResult {
    private String status;
    private String errorMessage;
}
