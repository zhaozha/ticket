package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author zhaozha
 * @date 2020/1/10 下午5:02
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ManagerLoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String phoneNum;
    private String pwd;
}
