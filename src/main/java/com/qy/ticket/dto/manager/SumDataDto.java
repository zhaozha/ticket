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
import java.util.ArrayList;
import java.util.List;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/13 上午10:52
 **/
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SumDataDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long count;
    private List<?> data = new ArrayList<>();
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer amount;
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer refundAmount;
    private Integer effectiveNum;
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer wxFee;
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer income;

}
