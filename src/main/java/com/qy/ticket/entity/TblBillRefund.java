package com.qy.ticket.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblBillRefund implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 退款单号
     */
    @Id
    private Long id;

    /**
     * 退款金额
     */
    private Integer amount;

    /**
     * 退款时间
     */
    private Date time;

    /**
     * 充值单号
     */
    @Column(name = "bill_id")
    private Long billId;
}