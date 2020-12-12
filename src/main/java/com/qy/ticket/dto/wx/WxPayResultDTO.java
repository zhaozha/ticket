package com.qy.ticket.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author zhaozha
 * @date 2020/1/7 下午12:08
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class WxPayResultDTO {
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String wxPackage;
    private String signType;
    private String paySign;
}
