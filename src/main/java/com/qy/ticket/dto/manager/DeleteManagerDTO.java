package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhaozha
 * @date 2020/1/10 下午5:52
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class DeleteManagerDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "被删除管理员id不能为空")
    private Long id;
    @NotNull(message = "管理员id不能为空")
    private Long managerId;
}
