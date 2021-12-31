package com.bessky.logistic.conf.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物流配置表实体类
 *
 * @author liunancun
 * @date 2020/04/11
 */
@Data
@TableName("t_logistics_conf")
public class LogisticsConf implements Serializable
{
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "logistics_id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物流公司
     */
    @TableField(value = "logistics_company")
    private String logisticCompany;

    /**
     * 用户标识
     */
    private String userId;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 客户端标识
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 编码
     */
    private String code;

    /**
     * 重定向地址
     */
    private String redirectUri;

    /**
     * 访问令牌
     */
    @TableField(value = "token")
    private String accessToken;

    /**
     * 访问令牌过期时间
     */
    @TableField(value = "token_expiration")
    private LocalDateTime accessTokenExpiration;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 刷新令牌过期时间
     */
    @TableField(value = "refresh_expiration")
    private LocalDateTime refreshTokenExpiration;

    /**
     * 登录名
     */
    @TableField(value = "login_name")
    private String login;

    /**
     * 登录密码
     */
    @TableField(value = "login_pwd")
    private String password;

    /**
     * 订单编号前缀
     */
    private String orderNoPrefix;

    /**
     * 寄件人姓名
     */
    @TableField(value = "from_contacter")
    private String fromContacts;

    /**
     * 寄件人邮编
     */
    private String fromPostCode;

    /**
     * 寄件人公司
     */
    private String fromCompany;

    /**
     * 寄件人电话
     */
    private String fromTel;

    /**
     * 国家二字码
     */
    private String fromCountryCode;

    /**
     * 寄件人省份
     */
    private String fromProvince;

    /**
     * 寄件人城市
     */
    private String fromCity;

    /**
     * 寄件人街道
     */
    @TableField(value = "from_street1")
    private String fromStreet;

    /**
     * 寄件人邮箱
     */
    private String fromEmail;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private Integer createdBy;
}