package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;


/**
 * @author zhaozha
 * @date 2020/1/11 下午6:56
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginPowerDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long productId;
    private Long parkId;
    private String parkName;
    private String productName;
}
