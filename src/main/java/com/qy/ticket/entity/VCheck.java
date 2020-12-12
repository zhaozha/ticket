package com.qy.ticket.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class VCheck implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private Date time;

    @Column(name = "phone_num")
    private String phoneNum;

    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "ticket_num")
    private Integer ticketNum;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "ticket_name")
    private String ticketName;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "park_name")
    private String parkName;
}