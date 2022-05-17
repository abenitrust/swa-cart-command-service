package com.swa.application.service;

import com.swa.application.domain.Customer;
import com.swa.application.dto.ProductChangeDto;
import com.swa.application.dto.ProductDto;
import com.swa.application.integration.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import com.swa.application.domain.CartLine;
import com.swa.application.domain.Product;
import com.swa.application.domain.ShoppingCart;
import com.swa.application.exception.DBException;
import com.swa.application.repository.ShoppingCartRepository;

@Service
public class CartService {
    private  ShoppingCartRepository shoppingCartRepository;
    private  EventService eventService;
    private  ProductFeignClient productService;

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    @Autowired
    public CartService(EventService eventService, ShoppingCartRepository shoppingCartRepository, ProductFeignClient productService) {
        this.eventService = eventService;
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public void create(ShoppingCart cart) throws DBException {
        try {
            shoppingCartRepository.save(cart);
            eventService.sendCartCreatedMsg(cart);
        } catch (Exception e) {
            throw new DBException("Cannot create cart.");
        }
    }

    public void update(ShoppingCart cart) throws DBException {
        try {
            shoppingCartRepository.save(cart);
            eventService.sendCartUpdatedMsg(cart);
        } catch (Exception e) {
            throw new DBException("Can't update shopping cart");
        }
    }

    public void delete(String cartNumber) throws DBException {
        try {
            var cart = shoppingCartRepository.findById(cartNumber)
                    .orElseThrow(() -> new DBException("Shopping cart by the given number not found"));
            shoppingCartRepository.delete(cart);
            eventService.sendCartDeletedMsg(cart);
        } catch (Exception e){
            throw new DBException(e.getMessage());
        }
    }

    public void checkout(String cartNumber) throws DBException {
        try {
            List<String> errorMsgs = new ArrayList<>();
            ShoppingCart cart = shoppingCartRepository.findById(cartNumber)
                    .orElseThrow(() ->
                            new DBException("Cart with number: " + cartNumber + " couldn't be found")
                    );

            boolean stockNotAvailable = false;

            for (CartLine cartLine : cart.getCartLines()) {
                Product p = cartLine.getProduct();
                int productStock = getProductStock(p.getProductNumber());
                if (cartLine.getQuantity() > productStock) {
                    errorMsgs.add(
                            "Selected amount of product "
                            + p + " is not available in Stock. Only "
                            +  productStock + " are available"
                    );

                    stockNotAvailable = true;
                }
            }

            if (stockNotAvailable == true) {
                throw new DBException(
                        "Sorry but some products are out of stock: \n" +
                                String.join(" \n" + errorMsgs)
                );
            }
            delete(cart.getShoppingCartNumber());
            eventService.sendCartCheckoutMsg(cart);
        } catch(Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    public int getProductStock(String productNumber) throws DBException {
        return productService.getProduct(productNumber).getNumberInStock();
    }

    public void addProduct(ProductChangeDto pDto) throws DBException{
        try {
            var cart = shoppingCartRepository.findById(pDto.getCartNumber())
                    .orElseThrow(() -> new DBException("Cart not found!"));

            int productStock = getProductStock(pDto.getProductNumber());
            if(pDto.getQuantity() > productStock) {
                throw new DBException("Not enough stock for the product. Available amt is: " + productStock);
            }
            Product newProd = new Product(pDto.getProductNumber(), pDto.getPrice());
            boolean productAdded = cart.addProduct(newProd, pDto.getQuantity());
            if(!productAdded) {
                throw new DBException("Error occurred when adding product. Please try again");
            }
            update(cart);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    public void removeProduct(ProductChangeDto pDto) throws DBException{
        try {
            var cart = shoppingCartRepository.findById(pDto.getCartNumber())
                    .orElseThrow(() -> new DBException("Cart not found!"));
            boolean productRemoved = cart.removeProduct(pDto.getProductNumber());
            if(!productRemoved) {
                throw new DBException("Error occurred when removing product. Please try again");
            }
            update(cart);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }

    public void changeQuantity(ProductChangeDto pDto) throws DBException{
        try {
            var cart = shoppingCartRepository.findById(pDto.getCartNumber())
                    .orElseThrow(() -> new DBException("Cart not found!"));

            int productStock = getProductStock(pDto.getProductNumber());
            if(pDto.getQuantity() > productStock) {
                throw new DBException("Not enough stock for the product. Available amt is: " + productStock);
            }
            boolean qtyChanged = cart.changeQuantity(pDto.getProductNumber(), pDto.getQuantity());
            if(!qtyChanged) {
                throw new DBException("Error occurred when updated product quantity. Please try again");
            }
            update(cart);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
    }


    public Customer getCustomer(String customer) {
        return new Customer();
    }

    @FeignClient("product-service")
    @RibbonClient(name="product-service")
    interface ProductFeignClient{
        @RequestMapping("/api/v1/products/{productNumber}")
        ProductDto getProduct(@PathVariable("productNumber") String productNumber);
    }
}
