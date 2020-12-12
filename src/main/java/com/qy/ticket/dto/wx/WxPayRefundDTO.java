package com.qy.ticket.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author zhaozha
 * @date 2020/1/10 上午10:25
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class WxPayRefundDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String appid;
    private String mch_id;
    private String sub_mch_id;
    private String nonce_str;
    private String sign;
    private String sign_type;
    private String transaction_id;
    private String out_trade_no;
    private String out_refund_no;
    private int total_fee;
    private int refund_fee;
    private String refund_fee_type;
    private String refund_desc;
    private String refund_account;
    private String notify_url;
}
