package com.app.BigBasket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
@Data
@Entity
@Table(name = "coupon_detail")
@AllArgsConstructor
@NoArgsConstructor
public class Coupons {

    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "coupon_id")
    private int id;
    private String code;
    private int percentage;
    private int maxLimit;
    @Column(name = "valid_till")
    private Date validity;


}
