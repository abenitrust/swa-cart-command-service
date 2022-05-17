package com.swa.application.dto;

import com.swa.application.domain.Product;
import lombok.Data;
import java.util.List;


@Data
public class ProductForOrderDto {
    private String customerId;
    private List<Product> products;
}
