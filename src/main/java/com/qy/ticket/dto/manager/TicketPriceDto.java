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
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/13 上午2:51
 **/
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TicketPriceDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    @JsonSerialize(using = FeeSerialize.class)
    @JsonDeserialize(using = FeeDeserialize.class)
    private Integer price;
}
