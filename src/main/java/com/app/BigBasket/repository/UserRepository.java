package com.app.BigBasket.repository;

import com.app.BigBasket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer > {
   // User findById(Integer userId);
    User findByEmail(String email);
}
