package com.microservice.product_microservice.repository;

import com.microservice.product_microservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
