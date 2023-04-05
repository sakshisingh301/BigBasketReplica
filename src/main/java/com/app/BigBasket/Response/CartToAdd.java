package com.app.BigBasket.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartToAdd {

    private String productName;
    private int userId;
}
