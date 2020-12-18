package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

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
public class AddManagerDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotBlank(message = "手机号不能为空")
    private String phoneNum;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotNull(message = "管理员设置级别不能为空")
    private Integer level;
    @NotNull(message = "产品id不能为空")
    private Long productId;
    @NotNull(message = "景区id不能为空")
    private Long parkId;
    // 添加人info
    @NotNull(message = "添加人id不能为空")
    private Long managerId;
}
