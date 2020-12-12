package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhaozha
 * @date 2020/1/13 下午5:08
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SumRecordDTO {
    private BigDecimal income;
    private BigDecimal wxFee;
    private Integer effectiveNum;
}
