package com.app.BigBasket.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {

    private int total;
    private int saving;
    private List<CartResponse> cartResponse;
}
