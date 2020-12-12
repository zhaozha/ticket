package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhaozha
 * @date 2020/1/11 下午5:49
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblRecordDTO {
    private BigDecimal income;
    private BigDecimal amount;
    private BigDecimal refundAmount;
    private BigDecimal wxFee;
    private Integer effectiveNum;
    private String time;
}
