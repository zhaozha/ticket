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
public class WxUnifiedorderResultDTO {
    // 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
    private String return_code;
    private String return_msg;
    private String appid;
    private String mch_id;
    private String device_info;
    private String nonce_str;
    private String sign;
    // SUCCESS/FAIL
    private String result_code;
    private String err_code;
    private String err_code_des;
    // SUCCESS返回
    private String trade_type;
    private String prepay_id;
    private String code_url;
}
