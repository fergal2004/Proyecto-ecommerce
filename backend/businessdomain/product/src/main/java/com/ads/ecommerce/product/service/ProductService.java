package com.ads.ecommerce.product.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ads.ecommerce.product.dto.ProductRequest;
import com.ads.ecommerce.product.dto.ProductResponse;
import com.ads.ecommerce.product.dto.StockUpdateRequest;
import com.ads.ecommerce.product.exception.InsufficientStockException;
import com.ads.ecommerce.product.exception.ProductNotFoundException;
import com.ads.ecommerce.product.model.Product;
import com.ads.ecommerce.product.model.ProductCategory;
import com.ads.ecommerce.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product: {}", request.getSku());
        
        // Validar SKU único
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Ya existe un producto con el SKU: " + request.getSku());
        }
        
        // Crear producto
        Product product = new Product();
        product.setSku(request.getSku().toUpperCase());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setActive(true);
        
        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        
        return toResponse(saved);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado: " + id));
        
        return toResponse(product);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku.toUpperCase())
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con SKU: " + sku));
        
        return toResponse(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProducts() {
        return productRepository.findAvailableProducts().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String name) {
        return productRepository.searchByName(name).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado: " + id));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        
        Product updated = productRepository.save(product);
        log.info("Product updated: {}", updated.getId());
        
        return toResponse(updated);
    }
    
    @Transactional
    public ProductResponse updateStock(Long id, StockUpdateRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado: " + id));
        
        try {
            if (request.getOperation() == StockUpdateRequest.StockOperation.ADD) {
                product.increaseStock(request.getQuantity());
                log.info("Stock increased for product {}: +{}", id, request.getQuantity());
            } else {
                product.decreaseStock(request.getQuantity());
                log.info("Stock decreased for product {}: -{}", id, request.getQuantity());
            }
            
            Product updated = productRepository.save(product);
            return toResponse(updated);
            
        } catch (IllegalStateException e) {
            throw new InsufficientStockException(e.getMessage());
        }
    }
    
    @Transactional
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado: " + id));
        
        product.deactivate();
        productRepository.save(product);
        log.info("Product deactivated: {}", id);
    }
    
    @Transactional
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado: " + id));
        
        product.activate();
        productRepository.save(product);
        log.info("Product activated: {}", id);
    }
    
    @Transactional
    public void discontinueProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado: " + id));
        
        product.discontinue();
        productRepository.save(product);
        log.info("Product discontinued: {}", id);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Producto no encontrado: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted: {}", id);
    }
    
    // Método auxiliar para convertir a Response
    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCategory(product.getCategory());
        response.setStatus(product.getStatus());
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.getActive());
        response.setAvailable(product.isAvailable());
        response.setLowStock(product.hasLowStock());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        
        return response;
    }
}
