package com.xbot.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Data
@Entity
@Table(name = "sys_user") // 表名
@EqualsAndHashCode(callSuper = true) // 包含父类的字段比较
// 1. 逻辑删除核心配置：调用 repository.delete() 时，执行的是 UPDATE
@SQLDelete(sql = "UPDATE sys_user SET deleted = 1 WHERE id = ?")
@SQLRestriction("deleted = 0") // 替代已弃用的 @Where("deleted = 0")
@Schema(description = "后台用户实体")
public class SysUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度需在4-20字符之间")
    @Column(unique = true, nullable = false, length = 64)
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @JsonIgnore // 【关键】查询用户信息时，绝对不能把密码返回给前端
    private String password;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "帐号启用状态：1->启用；0->禁用")
    private Integer status;

    @Schema(description = "角色：ADMIN/USER，这里先用简单字符串，复杂权限需关联表,0表示管理员，1是普通用户")
    private int role;

    @Schema(description = "逻辑删除标记：0->未删除；1->已删除")
    @JsonIgnore // 前端不需要看到这个字段
    private Integer deleted = 0;
}