package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/13 上午3:01
 **/
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CancellationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Long> ids;
}
