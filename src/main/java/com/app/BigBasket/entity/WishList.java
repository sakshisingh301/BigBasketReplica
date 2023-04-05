package com.app.BigBasket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wishlist")
public class WishList {
    @Id
    private int productId;
   private String productName;
   private Integer productQuantity;
   private Double productPrice;
   private String productCetegory;
}
