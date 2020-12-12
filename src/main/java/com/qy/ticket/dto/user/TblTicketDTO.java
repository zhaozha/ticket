package com.qy.ticket.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zhaozha
 * @date 2020/1/7 下午2:00
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblTicketDTO {
    @NotNull(message = "票编号不可为空")
    private Long ticketId;

    @NotNull(message = "票数不可为空")
    @Min(value = 0, message = "票数必须大于0")
    private Integer ticketNum;
}
