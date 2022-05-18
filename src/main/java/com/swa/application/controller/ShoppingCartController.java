package com.swa.application.controller;

import com.swa.application.domain.ShoppingCart;
import com.swa.application.dto.ProductChangeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swa.application.service.CartService;

@RestController
@RequestMapping("/api/v1/carts")
public class ShoppingCartController {
    @Autowired
    private CartService cartService;

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartController.class);

    @PostMapping
    public ResponseEntity<String> create(@RequestBody ShoppingCart cart) {
        try {
            cartService.create(cart);
            return new ResponseEntity<>("SUCCESSFULLY CREATED CART", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/product/remove")
    public ResponseEntity<String> removeProduct(@RequestBody ProductChangeDto pDto) {
        try {
            cartService.removeProduct(pDto);
            return new ResponseEntity<>("product successfully removed", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/product/add")
    public ResponseEntity<String> addProduct(@RequestBody ProductChangeDto pDto) {
        try {
            cartService.addProduct(pDto);
            return new ResponseEntity<>("Product successfully added", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/product/change-quantity")
    public ResponseEntity<String> changeProductQuantity(@RequestBody ProductChangeDto pDto) {
        try {
            cartService.changeQuantity(pDto);
            return new ResponseEntity<>("Product quantity successfully updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{cartNumber}/checkout")
    public ResponseEntity<String> checkout(@PathVariable String cartNumber) {
        try{
            return new ResponseEntity<>(cartService.checkout(cartNumber), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{cartNumber}")
    public ResponseEntity<String> delete(@PathVariable String cartNumber) {
        try {
            cartService.delete(cartNumber);
            return new ResponseEntity<>("Checked out successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
