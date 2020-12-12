package com.qy.ticket.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qy.ticket.common.FeeDeserialize;
import com.qy.ticket.common.FeeSerialize;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblBillChild implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 子账单编号
     */
    @Id
    private Long id;

    /**
     * 手机号
     */
    @Column(name = "phone_num")
    private String phoneNum;

    /**
     * 子账单充值金额(分)
     */
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer amount;

    /**
     * 子账单退款金额(分)
     */
    @Column(name = "refund_amount")
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
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

    /**
     * 票编号
     */
    @Column(name = "ticket_id")
    private Long ticketId;

    /**
     * 票数量
     */
    @Column(name = "ticket_num")
    private Integer ticketNum;

    /**
     * 票单价(分)
     */
    @Column(name = "ticket_price")
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer ticketPrice;

    /**
     * 可退金额(分)
     */
    @Column(name = "returnable_amount")
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer returnableAmount;

    /**
     * 父账单编号
     */
    @Column(name = "bill_id")
    private Long billId;

    /**
     * 行程编号
     */
    @Column(name = "record_id")
    private Long recordId;

    /**
     * 父账单金额(分)
     */
    @Column(name = "father_amount")
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer fatherAmount;
}