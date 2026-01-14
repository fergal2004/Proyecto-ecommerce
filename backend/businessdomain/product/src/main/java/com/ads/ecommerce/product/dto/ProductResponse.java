package com.ads.ecommerce.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ads.ecommerce.product.model.ProductCategory;
import com.ads.ecommerce.product.model.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private ProductCategory category;
    private ProductStatus status;
    private String imageUrl;
    private Boolean active;
    private Boolean available;
    private Boolean lowStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
