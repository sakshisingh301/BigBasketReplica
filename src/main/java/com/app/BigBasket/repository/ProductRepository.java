package com.app.BigBasket.repository;

import com.app.BigBasket.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {

    List<Product> findByCetegory(String cetegory);

    List<Product> findByName(String ItemName);

    List<Product> findByNameStartsWith(String firstLetter);



}
