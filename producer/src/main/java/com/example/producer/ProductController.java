package com.example.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController
{
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable int id)
    {
        Product p1 = new Product(id, 1000.0, "Laptop");
        return  p1;
    }
}
