package com.qy.ticket.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author zhaozha
 * @date 2020/1/7 下午12:07
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class WxUnifiedorderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String appid;
    private String mch_id;
    private String nonce_str;
    private String sign;
    private String sign_type;
    private String body;
    private String detail;
    private String attach;
    private String out_trade_no;
    private String fee_type;
    private int total_fee;
    private String spbill_create_ip;
    private String time_start;
    private String time_expire;
    private String goods_tag;
    private String notify_url;
    private String trade_type;
    private String product_id;
    private String limit_pay;
    private String openid;
    private String receipt;
    private String scene_info;

}