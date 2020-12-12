package com.qy.ticket.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblBillRefund {
    @Id
    private Long id;

    private BigDecimal amount;

    private Date time;

    private Long billId;
}