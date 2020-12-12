package com.qy.ticket.entity;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qy.ticket.common.FeeDeserialize;
import com.qy.ticket.common.FeeSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Table(name = "v_ticket")
public class VTicket implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "ticket_name")
    private String ticketName;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "park_id")
    private Long parkId;

    @Column(name = "ticket_price")
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer ticketPrice;

    @Column(name = "returnable_amount")
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer returnableAmount;

    private Integer proportion;

    private String url;

    @Column(name = "park_name")
    private String parkName;

    @Column(name = "product_name")
    private String productName;
}