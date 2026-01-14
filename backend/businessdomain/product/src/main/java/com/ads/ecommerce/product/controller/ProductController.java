package com.ads.ecommerce.product.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ads.ecommerce.product.dto.ProductRequest;
import com.ads.ecommerce.product.dto.ProductResponse;
import com.ads.ecommerce.product.dto.StockUpdateRequest;
import com.ads.ecommerce.product.model.ProductCategory;
import com.ads.ecommerce.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody @Valid ProductRequest request) {
        log.info("POST /api/v1/products");
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        log.info("GET /api/v1/products/{}", id);
        ProductResponse response = productService.getProduct(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        log.info("GET /api/v1/products/sku/{}", sku);
        ProductResponse response = productService.getProductBySku(sku);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) Boolean available) {
        log.info("GET /api/v1/products?available={}", available);
        
        List<ProductResponse> response = available != null && available
            ? productService.getAvailableProducts()
            : productService.getAllProducts();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable ProductCategory category) {
        log.info("GET /api/v1/products/category/{}", category);
        List<ProductResponse> response = productService.getProductsByCategory(category);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam String name) {
        log.info("GET /api/v1/products/search?name={}", name);
        List<ProductResponse> response = productService.searchProducts(name);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductResponse>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        log.info("GET /api/v1/products/price-range?minPrice={}&maxPrice={}", minPrice, maxPrice);
        List<ProductResponse> response = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts() {
        log.info("GET /api/v1/products/low-stock");
        List<ProductResponse> response = productService.getLowStockProducts();
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequest request) {
        log.info("PUT /api/v1/products/{}", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable Long id,
            @RequestBody @Valid StockUpdateRequest request) {
        log.info("PATCH /api/v1/products/{}/stock", id);
        ProductResponse response = productService.updateStock(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
        log.info("PATCH /api/v1/products/{}/deactivate", id);
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateProduct(@PathVariable Long id) {
        log.info("PATCH /api/v1/products/{}/activate", id);
        productService.activateProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/discontinue")
    public ResponseEntity<Void> discontinueProduct(@PathVariable Long id) {
        log.info("PATCH /api/v1/products/{}/discontinue", id);
        productService.discontinueProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/v1/products/{}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
