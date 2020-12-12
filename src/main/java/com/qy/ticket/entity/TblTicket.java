package com.qy.ticket.entity;

import java.math.BigDecimal;
import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblTicket {
    @Id
    private Long id;

    private String name;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "park_id")
    private Long parkId;

    private BigDecimal price;

    private BigDecimal returnableAmount;

    private Integer proportion;

    private String url;
}