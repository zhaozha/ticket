package com.qy.ticket.entity;

import javax.persistence.*;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblUser implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户编号
     */
    @Id
    private Long id;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户手机
     */
    @Column(name = "phone_num")
    private String phoneNum;

    /**
     * 用户名称
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 头像地址
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * 性别0未知1男2女
     */
    private Integer gender;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 国
     */
    private String country;

    @Column(name = "open_id")
    private String openId;

}