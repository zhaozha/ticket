package com.qy.ticket.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "tbl_discount")
public class TblDiscount {
    @Id
    private Long id;

    @Column(name = "park_id")
    private Long parkId;

    @Column(name = "park_name")
    private String parkName;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    private Integer status;
}