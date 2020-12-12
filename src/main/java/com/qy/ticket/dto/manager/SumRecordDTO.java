package com.qy.ticket.dto.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qy.ticket.common.FeeDeserialize;
import com.qy.ticket.common.FeeSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author zhaozha
 * @date 2020/1/13 下午5:08
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SumRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer income;
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer wxFee;
    private Integer effectiveNum;
}
