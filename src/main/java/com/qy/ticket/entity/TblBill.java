package com.qy.ticket.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblBill implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 账单编号
     */
    @Id
    private Long id;

    /**
     * 手机号
     */
    @Column(name = "phone_num")
    private String phoneNum;

    /**
     * 充值金额(分)
     */
    private Integer amount;

    /**
     * 退款金额(分)
     */
    @Column(name = "refund_amount")
    private Integer refundAmount;

    /**
     * 时间
     */
    private Date time;

    @Column(name = "open_id")
    private String openId;

    /**
     * 0未支付1支付成功2全额退款
     */
    private Integer status;

    /**
     * 用户编号
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 景区编号
     */
    @Column(name = "park_id")
    private Long parkId;

    /**
     * 产品编号
     */
    @Column(name = "product_id")
    private Long productId;
}