package com.qy.ticket.entity;

import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblCard {
    @Id
    private Integer id;

    @Column(name = "ticket_code")
    private String ticketCode;

    @Column(name = "voice_num")
    private Integer voiceNum;

    @Column(name = "card_no")
    private String cardNo;
}