package com.qy.ticket.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/17 下午3:22
 **/
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblUserDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "手机号不能为空")
    private String phoneNum;
    private String nickName;
    private String avatarUrl;
    private Integer gender;
    private String province;
    private String city;
    private String country;
    private String openId;
}
