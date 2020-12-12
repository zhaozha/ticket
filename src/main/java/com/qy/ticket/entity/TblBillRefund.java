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
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
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