package com.swa.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductChangeDto {
    private String cartNumber;
    private String productNumber;
    private int quantity;
}
