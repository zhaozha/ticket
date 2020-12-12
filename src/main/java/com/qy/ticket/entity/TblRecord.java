package com.qy.ticket.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblRecord {
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
     * 当日消费
     */
    private BigDecimal income;

    /**
     * 当日充值
     */
    private BigDecimal amount;

    /**
     * 当日退款
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

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

    private Long parkId;

    private String parkName;

    private Long productId;

    private String productName;

    private String ticketName;

    private BigDecimal ticketPrice;

    private BigDecimal returnableAmount;

    private Integer effectiveNum;

    private String reason;

    private String seq;

}