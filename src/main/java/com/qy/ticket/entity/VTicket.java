package com.qy.ticket.entity;

import java.math.BigDecimal;
import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class VTicket {
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "ticket_name")
    private String ticketName;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "park_id")
    private Long parkId;

    @Column(name = "ticket_price")
    private BigDecimal ticketPrice;

    @Column(name = "park_name")
    private String parkName;

    @Column(name = "product_name")
    private String productName;

    private BigDecimal returnableAmount;

    private Integer proportion;

    private String url;

}