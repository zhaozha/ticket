package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;

/**
 * @author zhaozha
 * @date 2020/1/10 下午5:52
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AddManagerDTO {
    private String phoneNum;
    private String name;
    private Integer level;
    private Long productId;
    private Long parkId;
    private Long managerId;
}
