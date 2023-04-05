package com.app.BigBasket.controller;

import com.app.BigBasket.Response.*;
import com.app.BigBasket.entity.Cart;
import com.app.BigBasket.entity.Product;
import com.app.BigBasket.service.ItemsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ItemsService itemsService;

    @GetMapping(value = "/AllItems")
    public List<Product> getAllItems()
    {
        return itemsService.getItems();
    }

    @GetMapping(value = "/item")
    public List<SearchResponse> searchAllItems(@ModelAttribute SearchCriteria searchCriteria)
    {
        return itemsService.searchItems(searchCriteria);
    }

    @PostMapping("/addProduct/{productId}")
    public CartResponse addToCart(@RequestBody CartToAdd cartToAdd, @PathVariable Integer productId)
    {
        return itemsService.addToCart(productId,cartToAdd);
    }

    @GetMapping("getCart/{userId}")
    public List<CartResponse> getCart(@PathVariable Integer userId)
    {
        return itemsService.getCartByUserId(userId);
    }

   @GetMapping("remove/{userId}/{productName}")
    public CartResponse removeProduct(@PathVariable Integer userId,@PathVariable String productName)
   {
       return itemsService.removeProduct(userId,productName);
   }
   @GetMapping("addPromotion/{userId}")
    public CheckoutResponse applyCoupon (@PathVariable Integer userId)
   {
       return itemsService.addCoupons(userId);
   }
   @PostMapping("/sendmail/{userId}")
   public String sendMail(@RequestBody EmailRequest emailRequest,@PathVariable Integer userId ) throws MessagingException, IOException {
       itemsService.sendEmailWithAttachment( emailRequest,userId);
       return "email sent";
   }

   @PostMapping("wishlist/{userId}")
    public String wishList(@RequestBody WishListRequest wishListRequest,   @PathVariable Integer userId)
   {
     itemsService.wishList(userId,wishListRequest);
     return "added to wishlist for userId  " +userId;
   }






}
