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
public class TblRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 行程编号
     */
    @Id
    private Long id;

    /**
     * 票编号
     */
    @Column(name = "ticket_id")
    private Long ticketId;

    /**
     * 时间
     */
    private Date time;

    /**
     * 用户手机号
     */
    @Column(name = "phone_num")
    private String phoneNum;

    /**
     * 当日消费(分)
     */
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer income;

    /**
     * 当日充值(分)
     */
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer amount;

    /**
     * 当日退款(分)
     */
    @Column(name = "refund_amount")
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer refundAmount;

    /**
     * 可核销票
     */
    @Column(name = "available_num")
    private Integer availableNum;

    /**
     * 已核心票
     */
    @Column(name = "used_num")
    private Integer usedNum;

    /**
     * 总票数
     */
    @Column(name = "total_num")
    private Integer totalNum;

    /**
     * 用户编号
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 版本号
     */
    @Column(name = "version_id")
    private Integer versionId;

    /**
     * 票名称
     */
    @Column(name = "ticket_name")
    private String ticketName;

    /**
     * 景区编号
     */
    @Column(name = "park_id")
    private Long parkId;

    /**
     * 景区名称
     */
    @Column(name = "park_name")
    private String parkName;

    /**
     * 产品编号
     */
    @Column(name = "product_id")
    private Long productId;

    /**
     * 产品名称
     */
    @Column(name = "product_name")
    private String productName;

    /**
     * 票价(分)
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
     * 有效票数
     */
    @Column(name = "effective_num")
    private Integer effectiveNum;

    /**
     * 理由
     */
    private String reason;

    /**
     * 步长
     */
    private String seq;
}