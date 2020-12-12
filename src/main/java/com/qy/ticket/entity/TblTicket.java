package com.qy.ticket.entity;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qy.ticket.common.FeeDeserialize;
import com.qy.ticket.common.FeeSerialize;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblTicket implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    private String name;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "park_id")
    private Long parkId;

    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer price;

    @Column(name = "returnable_amount")
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer returnableAmount;

    private Integer proportion;

    private String url;
}