package com.app.BigBasket.Response;

import com.app.BigBasket.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private String name;
    private int quantity;
    private long total;


}
