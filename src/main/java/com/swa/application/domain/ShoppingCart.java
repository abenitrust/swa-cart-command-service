package com.swa.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document
public class ShoppingCart {
    @Id
    private String shoppingCartNumber;
    private List<CartLine> cartLines;

    public boolean removeProduct(String productNumber) {
        var cartLine = cartLines.stream()
                .filter(cL -> cL.getProduct().getProductNumber().equals(productNumber))
                .findFirst().orElse(null);

        if(cartLine != null) {
            cartLines.remove(cartLine);
            return true;
        } else {
            return false;
        }
    }

    public boolean addProduct(Product p, int quantity) {
        CartLine newLine = new CartLine(p, quantity);
        if(cartLines == null) {
            cartLines = new ArrayList<>();
        }
        cartLines.add(newLine);
        return true;
    }

    public boolean changeQuantity(String productNumber, int desiredQuantity) {
        var cartLine = cartLines.stream()
                .filter(cL -> cL.getProduct().getProductNumber().equals(productNumber))
                .findFirst().orElse(null);
        if(cartLine != null) {
            cartLine.setQuantity(desiredQuantity);
            return true;
        } else {
            return false;
        }
    }

}
