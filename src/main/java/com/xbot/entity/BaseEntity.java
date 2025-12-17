package com.xbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass // 表示这是一个父类，不是数据库表
@EntityListeners(AuditingEntityListener.class) // 开启监听
public abstract class BaseEntity {

    @CreatedDate // 插入时自动赋值
    @Column(updatable = false) // 更新时不允许修改创建时间
    private LocalDateTime createTime;

    @LastModifiedDate // 每次更新时自动赋值
    private LocalDateTime updateTime;

}