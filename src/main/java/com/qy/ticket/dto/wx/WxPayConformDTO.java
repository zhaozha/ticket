package com.qy.ticket.dto.wx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author zhaozha
 * @date 2020/1/7 下午1:27
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class WxPayConformDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JacksonXmlProperty(localName = "return_code")
    private String return_code;
    @JacksonXmlProperty(localName = "return_msg")
    private String return_msg;
    // 成功返回下面字段
    @JacksonXmlProperty(localName = "appid")
    private String appid;
    @JacksonXmlProperty(localName = "mch_id")
    private String mch_id;
    @JacksonXmlProperty(localName = "device_info")
    private String device_info;
    @JacksonXmlProperty(localName = "nonce_str")
    private String nonce_str;
    @JacksonXmlProperty(localName = "sign")
    private String sign;
    @JacksonXmlProperty(localName = "sign_type")
    private String sign_type;
    @JacksonXmlProperty(localName = "result_code")
    private String result_code;
    @JacksonXmlProperty(localName = "err_code")
    private String err_code;
    @JacksonXmlProperty(localName = "err_code_des")
    private String err_code_des;
    @JacksonXmlProperty(localName = "openid")
    private String openid;
    @JacksonXmlProperty(localName = "is_subscribe")
    private String is_subscribe;
    @JacksonXmlProperty(localName = "trade_type")
    private String trade_type;
    @JacksonXmlProperty(localName = "bank_type")
    private String bank_type;
    @JacksonXmlProperty(localName = "total_fee")
    private String total_fee;
    @JacksonXmlProperty(localName = "settlement_total_fee")
    private String settlement_total_fee;
    @JacksonXmlProperty(localName = "fee_type")
    private String fee_type;
    @JacksonXmlProperty(localName = "cash_fee")
    private String cash_fee;
    @JacksonXmlProperty(localName = "cash_fee_type")
    private String cash_fee_type;
    // todo 没有代金券相关字段
    @JacksonXmlProperty(localName = "transaction_id")
    private String transaction_id;
    @JacksonXmlProperty(localName = "out_trade_no")
    private String out_trade_no;
    @JacksonXmlProperty(localName = "attach")
    private String attach;
    @JacksonXmlProperty(localName = "time_end")
    private String time_end;
}
