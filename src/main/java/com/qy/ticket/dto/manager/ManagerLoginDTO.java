package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author zhaozha
 * @date 2020/1/10 下午5:02
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ManagerLoginDTO {
    private String phoneNum;
    private String pwd;
}
