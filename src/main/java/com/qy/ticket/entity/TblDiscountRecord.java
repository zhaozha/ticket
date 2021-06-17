package com.qy.ticket.entity;

import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblDiscountRecord {
    @Id
    private Long id;

    private Integer discount;

    private Integer status;
}