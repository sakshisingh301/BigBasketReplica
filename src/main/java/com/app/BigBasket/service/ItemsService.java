package com.app.BigBasket.service;

import com.app.BigBasket.Response.*;
import com.app.BigBasket.entity.*;
import com.app.BigBasket.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


@Service
public class ItemsService {

    private static final String PREMIUM_CETEGORY = "premium";
    private static final String NONPREMIUM_CETEGORY = "non-premium";
    private static final String PREMIUM_CODE="BIGBASKET_PREMIUM";
    private static final String NONPREMIUM_CODE="BIGBASKET_MODE";

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CouponsRepository couponsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WishListRepository wishListRepository;


    @PostConstruct
    public List<Product> getItems() {
        List<Product> productList = productRepository.findAll();
        return productList;
    }

    public List<SearchResponse> searchItems(SearchCriteria searchCriteria) {
        if (searchCriteria.getPattern() != null && !searchCriteria.getPattern().isEmpty()) {
            List<Product> productList = productRepository.findByNameStartsWith(searchCriteria.getPattern());
            if (productList.isEmpty()) {
                throw new RuntimeException("item not found");
            }
            List<SearchResponse> searchResponse = new ArrayList<>();
            for (Product product : productList) {
                SearchResponse searchResponse1 = new SearchResponse();
                searchResponse1.setName(product.getName());
                searchResponse1.setPrice(product.getPrice());
                searchResponse.add(searchResponse1);
            }
            return searchResponse;

        }
        if (searchCriteria.getCetegory() != null && !searchCriteria.getCetegory().isEmpty()) {
            List<Product> productList = productRepository.findByCetegory(searchCriteria.getCetegory());
            if (productList.isEmpty()) {
                throw new RuntimeException("item not found");
            }
            List<SearchResponse> searchResponse = new ArrayList<>();
            for (Product product : productList) {
                SearchResponse searchResponse1 = new SearchResponse();
                searchResponse1.setName(product.getName());
                searchResponse1.setPrice(product.getPrice());
                searchResponse.add(searchResponse1);
            }
            return searchResponse;
        }
        if (searchCriteria.getItemName() != null && !searchCriteria.getItemName().isEmpty()) {
            List<Product> productList = productRepository.findByName(searchCriteria.getItemName());
            if (productList.isEmpty()) {
                throw new RuntimeException("item not found");
            }
            List<SearchResponse> searchResponse = new ArrayList<>();
            for (Product product : productList) {
                SearchResponse searchResponse1 = new SearchResponse();
                searchResponse1.setName(product.getName());
                searchResponse1.setPrice(product.getPrice());
                searchResponse.add(searchResponse1);
            }
            return searchResponse;
        }
        throw new RuntimeException("cannot find");
    }

    private static Map<Product, Integer> products = new HashMap<>();

    public CartResponse addToCart(int productId, CartToAdd cartToAdd) {
        List<Product> productList = productRepository.findByName(cartToAdd.getProductName());
        if (productList.isEmpty()) {
            throw new RuntimeException("could not find the item");
        }
        if (productList.get(0).getId() != productId) {
            throw new RuntimeException("product id and product name are not matching");
        }
        Product product = productList.get(0);
        Map<Product, Integer> addProduct = addProduct(productList.get(0));
        Cart cart = cartRepository.findByName(cartToAdd.getProductName());
        CartResponse cartResponse = new CartResponse();

        if (cart == null) {
            cart = new Cart();
            cart.setName(product.getName());
            cart.setQuantity("1");
            cart.setTotal(Long.parseLong(product.getPrice()));
            cart.setUserId(cartToAdd.getUserId());

            cartResponse.setName(productList.get(0).getName());
            cartResponse.setQuantity(addProduct.get(productList.get(0)));
            cartResponse.setTotal(Long.parseLong(product.getPrice()));

        } else {

            int qty = Integer.parseInt(cart.getQuantity());
            long newTotal = Long.parseLong(product.getPrice())* (qty + 1);
            cart.setTotal(newTotal);
            cart.setQuantity(qty+1+"");
            cart.setCreatedDate(ZonedDateTime.now(ZoneId.of("UTC")));
            cartResponse.setName(cart.getName());
            cartResponse.setQuantity(Integer.parseInt(cart.getQuantity()));
            cartResponse.setTotal(newTotal);
        }

        cartRepository.save(cart);



        return cartResponse;
    }

    public static long getTotal(List<Product> productList, Map<Product, Integer> productIntegerMap) {
        int price = Integer.parseInt(productList.get(0).getPrice());
        int quantity = productIntegerMap.get(productList.get(0));

        return (price * quantity);

    }

    public static Map<Product, Integer> addProduct(Product product) {
        if (!products.containsKey(product)) {
            products.put(product, 1);
        } else {
            products.replace(product, products.get(product) + 1);
        }
        return products;
    }


    public List<CartResponse> getCartByUserId(Integer userId) {
        List<Cart> cartList = cartRepository.findByUserId(userId);
        if (cartList.isEmpty()) {
            throw new RuntimeException("userId not found");
        }

        List<CartResponse> cartResponseList = new ArrayList<>();
        for (Cart cart : cartList) {
            CartResponse cartResponse = new CartResponse();
            cartResponse.setName(cart.getName());
            cartResponse.setQuantity(Integer.parseInt(cart.getQuantity()));
            cartResponse.setTotal(cart.getTotal());
            cartResponseList.add(cartResponse);

        }

        return cartResponseList;
    }

    public CartResponse removeProduct(Integer userId, String productName)
    {
        List<Product> product=productRepository.findByName(productName);
        CartResponse cartResponse=new CartResponse();
        Cart cart=cartRepository.findByUserIdAndName(userId,productName);
        if(cart==null)
        {
            throw new RuntimeException("cart is empty for provided userId");
        }
        cartResponse.setName(productName);
        cartResponse.setQuantity(Integer.parseInt(cart.getQuantity())-1);
        long getTotal=cart.getTotal();
        long getPrice= Long.parseLong(product.get(0).getPrice());
        cartResponse.setTotal(getTotal-getPrice);
        cart.setName(productName);
        cart.setQuantity(String.valueOf(cartResponse.getQuantity()));
        cart.setTotal(cartResponse.getTotal());
        cart.setCreatedDate(ZonedDateTime.now(ZoneId.of("UTC")));
        cartRepository.save(cart);
        return  cartResponse;


    }

    public CheckoutResponse addCoupons(Integer userId)
    {
     CheckoutResponse checkoutResponse=new CheckoutResponse();
        List<Cart> carts = cartRepository.findAll();
     List<Cart> cart=cartRepository.findByUserId(userId);

     CartResponse cartResponse=new CartResponse();
     if(cart.isEmpty())
     {
         throw new RuntimeException("cart is empty");
     }
     Optional<User> user= userRepository.findById(userId);

     String getCetegory= user.get().getCetegory();
        int total=0;
        for(Cart cart1:cart)
        {
            total= (int) (total+cart1.getTotal());
        }
     if(getCetegory.equals(PREMIUM_CETEGORY) && eligibleForCoupon(total,user))
     {
         Coupons coupons=couponsRepository.findByCode(PREMIUM_CODE);
         String couponCodeForPremium=coupons.getCode();
         int percentage=coupons.getPercentage();
         int revisedTotal=total-(percentage*total)/100;

         checkoutResponse.setTotal(revisedTotal);
         checkoutResponse.setSaving(total-revisedTotal);
         checkoutResponse.setCartResponse(buildCartResponse(carts));



     }
     else if(getCetegory.equals(NONPREMIUM_CETEGORY)&&eligibleForCoupon(total,user))
     {
         Coupons coupons=couponsRepository.findByCode(NONPREMIUM_CODE);
      String couponCodeForNonPremium=coupons.getCode();
         int percentage=coupons.getPercentage();
         int revisedTotal=total-(percentage*total)/100;
         checkoutResponse.setTotal(revisedTotal);
         checkoutResponse.setSaving(total-revisedTotal);
         checkoutResponse.setCartResponse(buildCartResponse(carts));

     }
     else
     {
         throw new RuntimeException("coupon is not applicable");
     }
     return checkoutResponse;
    }

    public static boolean eligibleForCoupon(int total,Optional<User> user)
    {
        return total>100 &&user.get().getActive().equals("yes");
    }

    public static List<CartResponse> buildCartResponse(List<Cart> carts)
    {
        List<CartResponse> cartResponseList=new ArrayList<>();
        for(Cart cart:carts)
        {
            CartResponse cartResponse=new CartResponse();
            cartResponse.setName(cart.getName());
            cartResponse.setQuantity(Integer.parseInt(cart.getQuantity()));
            cartResponse.setTotal(cart.getTotal());
            cartResponseList.add(cartResponse);

        }
     return cartResponseList;

    }

    public void sendEmailWithAttachment(EmailRequest emailRequest,Integer userId) throws MessagingException, IOException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper
                = new MimeMessageHelper(mimeMessage, true);
       User user= userRepository.findByEmail(emailRequest.getToEmail());
       if(user==null)
           throw new RuntimeException("user not found ");
        mimeMessageHelper.setFrom(user.getEmail());
        mimeMessageHelper.setTo(emailRequest.getToEmail());
        mimeMessageHelper.setText(emailRequest.getBody());
        mimeMessageHelper.setSubject(emailRequest.getSubject());
        List<Cart> carts=cartRepository.findByUserId(userId);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String cartJson = ow.writeValueAsString(carts);
        JSONObject cartDetail = new JSONObject();
        cartDetail.put("carts",cartJson);

        FileWriter file=new FileWriter("C:\\Users\\sakshisingh57\\Documents\\carts.json");
       // FileWriter file=new FileWriter("carts.json");
        file.write(cartDetail.toJSONString());
        file.close();



        FileSystemResource fileSystem
                = new FileSystemResource(new File("C:\\Users\\sakshisingh57\\Documents\\carts.txt"));

        mimeMessageHelper.addAttachment(fileSystem.getFilename(),
                fileSystem);

        mailSender.send(mimeMessage);
        System.out.println("Mail Send...");

    }

    public void wishList(Integer userId, WishListRequest wishListRequest)
    {

      List<Product> products=productRepository.findByName(wishListRequest.getProductName());
      if(products.isEmpty())
      {
          throw new RuntimeException("product not found");
      }
      WishList wishList=wishListRepository.findByProductName(wishListRequest.getProductName());
      if(wishList==null)
      {
        wishList=new WishList();
          wishList.setProductQuantity(1);
          wishList.setProductPrice(Double.parseDouble(products.get(0).getPrice()));
        //TODO -productId should be similar to id in product table  wishList.setProductId(1);
      }
      else
      {
          wishList.setProductQuantity(wishList.getProductQuantity()+1);
          wishList.setProductPrice(Double.parseDouble(products.get(0).getPrice())*wishList.getProductQuantity());
          //TODO -productId should be similar to id in product table  wishList.setProductId(1);
      }
      wishList.setProductName(products.get(0).getName());
      wishList.setProductCetegory(products.get(0).getCetegory());
      wishListRepository.save(wishList);
      Cart cart=cartRepository.findByName(wishListRequest.getProductName());
      if(cart!=null)
      {
              cart.setName(wishListRequest.getProductName());
              cart.setUserId(userId);
              int getCartQuantity= Integer.parseInt(cart.getQuantity());
              int getWishlistQuantity=wishList.getProductQuantity();
              int removeFromCart=getCartQuantity-getWishlistQuantity;
              if(removeFromCart==0) {
                  deleteByName(wishListRequest.getProductName());
                  return;
              }

              cart.setQuantity(String.valueOf(removeFromCart));
              cart.setTotal((long) Integer.parseInt(cart.getQuantity()) *Integer.parseInt(products.get(0).getPrice()));

          cartRepository.save(cart);

      }




    }
    @Transactional
    public List<Cart> deleteByName(String name)
    {
      return cartRepository.deleteByName(name);
    }


}
