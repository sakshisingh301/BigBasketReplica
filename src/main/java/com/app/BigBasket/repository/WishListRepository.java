package com.app.BigBasket.repository;

import com.app.BigBasket.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList,Integer> {
WishList findByProductName(String productName);
}
