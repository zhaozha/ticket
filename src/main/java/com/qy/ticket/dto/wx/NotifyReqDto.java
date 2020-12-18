package com.qy.ticket.dto.wx;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/22 下午10:50
 **/
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "xml")
public class NotifyReqDto {
    /**
     * 返回状态码
     * SUCCESS/FAIL
     * <p>
     * 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     */
    @JacksonXmlProperty(localName = "return_code")
    private String returnCode;
    /**
     * 业务结果
     */
    @JacksonXmlProperty(localName = "result_code")
    private String resultCode;
    /**
     * 返回信息
     * 如非空，为错误原因,签名失败,参数格式校验错误
     */
    @JacksonXmlProperty(localName = "return_msg")
    private String returnMsg;
    /**
     * 微信分配的小程序ID
     */
    @JacksonXmlProperty(localName = "appid")
    private String appId;
    /**
     * 微信支付分配的商户号
     */
    @JacksonXmlProperty(localName = "mch_id")
    private String mchId;
    /**
     * 微信支付分配的终端设备号
     */
    @JacksonXmlProperty(localName = "device_info")
    private String deviceInfo;
    /**
     * 随机字符串，不长于32位
     */
    @JacksonXmlProperty(localName = "nonce_str")
    private String nonceStr;
    /**
     * 签名
     */
    @JacksonXmlProperty(localName = "sign")
    private String sign;
    /**
     * 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
     */
    @JacksonXmlProperty(localName = "sign_type")
    private String signType;
    /**
     * 错误返回的信息描述
     */
    @JacksonXmlProperty(localName = "err_code")
    private String errCode;
    /**
     * 错误返回的信息描述
     */
    @JacksonXmlProperty(localName = "err_code_des")
    private String errCodeDes;
    /**
     * 用户在商户appid下的唯一标识
     */
    @JacksonXmlProperty(localName = "openid")
    private String openId;
    /**
     * 用户是否关注公众账号，Y-关注，N-未关注
     */
    @JacksonXmlProperty(localName = "is_subscribe")
    private String isSubscribe;
    /**
     * JSAPI、NATIVE、APP
     */
    @JacksonXmlProperty(localName = "trade_type")
    private String tradeType;
    /**
     * 银行类型，采用字符串类型的银行标识，银行类型见银行列表
     */
    @JacksonXmlProperty(localName = "bank_type")
    private String bankType;
    /**
     * 订单总金额，单位为分
     */
    @JacksonXmlProperty(localName = "total_fee")
    private Integer totalFee;
    /**
     * 应结订单金额=订单金额-非充值代金券金额，应结订单金额<=订单金额。
     */
    @JacksonXmlProperty(localName = "settlement_total_fee")
    private Integer settlementTotalFee;
    /**
     * 货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
     */
    @JacksonXmlProperty(localName = "fee_type")
    private String feeType;
    /**
     * 现金支付金额订单现金支付金额，详见支付金额
     */
    @JacksonXmlProperty(localName = "cash_fee")
    private Integer cashFee;
    /**
     * 货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
     */
    @JacksonXmlProperty(localName = "cash_fee_type")
    private String cashFeeType;
//    /**
//     * 代金券金额<=订单金额，订单金额-代金券金额=现金支付金额，详见支付金额
//     */
//    @JacksonXmlProperty(localName = "coupon_fee")
//    private Integer couponFee;
//    /**
//     * 代金券使用数量
//     */
//    @JacksonXmlProperty(localName = "coupon_count")
//    private Integer couponCount;
//    /**
//     * CASH--充值代金券
//     * NO_CASH---非充值代金券
//     * <p>
//     * 并且订单使用了免充值券后有返回（取值：CASH、NO_CASH）。$n为下标,从0开始编号，举例：coupon_type_0
//     * <p>
//     * 注意：只有下单时订单使用了优惠，回调通知才会返回券信息。
//     * 下列情况可能导致订单不可以享受优惠：可能情况。
//     */
//    @JacksonXmlProperty(localName = "coupon_type_0")
//    private Integer couponType0;
//    /**
//     * 代金券ID,$n为下标，从0开始编号
//     * 注意：只有下单时订单使用了优惠，回调通知才会返回券信息。
//     * 下列情况可能导致订单不可以享受优惠：可能情况。
//     */
//    @JacksonXmlProperty(localName = "coupon_id_0")
//    private Integer couponId0;
//    /**
//     * 单个代金券支付金额,$n为下标，从0开始编号
//     */
//    @JacksonXmlProperty(localName = "coupon_fee_0")
//    private Integer couponFee0;
    /**
     * 微信支付订单号
     */
    @JacksonXmlProperty(localName = "transaction_id")
    private String transactionId;
    /**
     * 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
     */
    @JacksonXmlProperty(localName = "out_trade_no")
    private String outTradeNo;
    /**
     * 商家数据包，原样返回
     */
    @JacksonXmlProperty(localName = "attach")
    private String attach;
    /**
     * 支付完成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
     */
    @JacksonXmlProperty(localName = "time_end")
    private String timeEnd;
}
