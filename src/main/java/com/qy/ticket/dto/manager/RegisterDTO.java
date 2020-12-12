package com.qy.ticket.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author zhaozha
 * @date 2020/1/10 下午5:52
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RegisterDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String phoneNum;
    private String openId;
}
