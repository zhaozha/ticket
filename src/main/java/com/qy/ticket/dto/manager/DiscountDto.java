package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2021/6/17 下午4:32
 **/
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class DiscountDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String phoneNum;
}
