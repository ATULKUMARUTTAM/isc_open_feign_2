package com.example.consumer;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController
{
//    private final RestTemplate restTemplate;

    private final ProductClient productClient;
    @GetMapping("/{productId}")
    public OrderResponse createOrder(@PathVariable int productId)
    {
     String url = "http://localhost:8081/api/products/" + productId;
//     Product product = restTemplate.getForObject(url, Product.class);

    // Calling Product Service using OpenFeign
     Product product = productClient.getProduct(productId);
     int quantity = 2;
     double totalAmount = product.getPrice() * quantity;
     return new OrderResponse(1001, product, quantity, totalAmount);
    }
}