package com.microservice.product_microservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.product_microservice.entity.Product;
import com.microservice.product_microservice.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/products should return all products")
    void testGetAllProducts() throws Exception {
        List<Product> products = List.of(
                new Product(1L, "Item 1", "Desc 1", 12),
                new Product(2L, "Item 2", "Desc 2", 13)
        );

        given(productRepository.findAll()).willReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].name", is("Item 1")));
    }

    @Test
    @DisplayName("POST /api/products should create product")
    void testCreateProduct() throws Exception {
        Product input = new Product(null, "Item A", "Desc A", 33);
        Product saved = new Product(1L, "Item A", "Desc A", 99);

        given(productRepository.save(Mockito.any(Product.class))).willReturn(saved);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item A"));
    }
    @Test
    @DisplayName("PUT /api/products/{id} should update product")
    void testUpdateProduct() throws Exception {
        Long productId = 1L;

        Product existingProduct = new Product(productId, "Old Name", "Old Desc", 77);
        Product updatedProduct = new Product(productId, "New Name", "New Desc", 33);

        // Simulate product found in DB
        given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));

        // Simulate saving updated product
        given(productRepository.save(Mockito.any(Product.class))).willReturn(updatedProduct);

        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.description").value("New Desc"))
                .andExpect(jsonPath("$.price").value(33));
    }


    @Test
    @DisplayName("GET /api/products/{id} should return product by ID")
    void testGetProductById() throws Exception {
        Product product = new Product(1L, "Test", "Desc", 87);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test")));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} should delete product")
    void testDeleteProduct() throws Exception {
        doNothing().when(productRepository).deleteById(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/products/string should return hello")
    void testHelloWorld() throws Exception {
        mockMvc.perform(get("/api/products/string"))
                .andExpect(status().isOk())
                .andExpect(content().string("helloWrld200ok"));
    }
}
