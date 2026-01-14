package com.ads.ecommerce.product.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ads.ecommerce.product.model.Product;
import com.ads.ecommerce.product.model.ProductCategory;
import com.ads.ecommerce.product.model.ProductStatus;

@Repository

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);
    
    List<Product> findByActive(Boolean active);
    
    List<Product> findByCategory(ProductCategory category);
    
    List<Product> findByStatus(ProductStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.status = 'AVAILABLE' AND p.stock > 0")
    List<Product> findAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> searchByName(@Param("name") String name);
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.stock < 10")
    List<Product> findLowStockProducts();
    
    boolean existsBySku(String sku);
}
