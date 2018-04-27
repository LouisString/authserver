package com.itstyle.jwt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itstyle.jwt.model.User;
/**
 * 用户管理
 */
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUsername(String username);
}
