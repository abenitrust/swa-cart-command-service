package com.swa.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductForStock {
    private String productNumber;
    private String name;
    private double price;
    private String description;
    private int numberInStock;
}
