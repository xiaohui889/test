package com.xbot.repository;

import com.xbot.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SysUser, Integer> {

    // 根据用户名查找（用于登录）
    // 由于加了 @Where(clause = "deleted = 0")，这里只会查未删除的用户
    Optional<SysUser> findByUsername(String username);
    // 检查用户名是否存在（用于注册判重）
    boolean existsByUsername(String username);
}
