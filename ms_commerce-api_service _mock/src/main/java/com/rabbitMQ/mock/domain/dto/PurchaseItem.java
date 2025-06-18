package com.rabbitMQ.mock.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PurchaseItem {

    private int productId;
    private int quantity;
    private double total;
}
