package com.qy.ticket.entity;

import javax.persistence.*;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblManager implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 管理员编号
     */
    @Id
    private Long id;

    /**
     * 管理员姓名
     */
    private String name;

    /**
     * 管理员手机
     */
    @Column(name = "phone_num")
    private String phoneNum;

    /**
     * 管理员名称
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

    /**
     * 等级
     */
    private Integer level;

    /**
     * 产品
     */
    @Column(name = "product_id")
    private Long productId;

    /**
     * 景区
     */
    @Column(name = "park_id")
    private Long parkId;

    /**
     * 密码
     */
    private String pwd;
}