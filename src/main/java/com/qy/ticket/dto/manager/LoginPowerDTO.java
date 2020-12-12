package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


/**
 * @author zhaozha
 * @date 2020/1/11 下午6:56
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginPowerDTO {
    private Long productId;
    private Long parkId;
    private String parkName;
    private String productName;
}
