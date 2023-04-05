package com.app.BigBasket.repository;

import com.app.BigBasket.entity.Coupons;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponsRepository extends JpaRepository<Coupons,Integer> {
    Coupons findByCode(String code);
}
