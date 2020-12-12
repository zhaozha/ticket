package com.qy.ticket.entity;

import java.util.Date;
import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblCheck {
    @Id
    private Long id;

    private Date time;

    @Column(name = "ticket_num")
    private Integer ticketNum;

    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "phone_num")
    private String phoneNum;

    private Long ticketId;

    private String cardNo;

}