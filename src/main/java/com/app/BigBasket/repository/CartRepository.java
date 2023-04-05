package com.app.BigBasket.repository;

import com.app.BigBasket.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {
List<Cart> findByUserId(Integer userId);
Cart findByName(String name);
Cart findByUserIdAndName(Integer userId,String name);
List<Cart> deleteByName(String name);
}
