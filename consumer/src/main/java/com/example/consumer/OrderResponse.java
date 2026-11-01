package com.example.consumer;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private int orderId;
    private Product product;
    private int quantity;
    private double totalAmount;
}