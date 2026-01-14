package com.ads.ecommerce.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String sku;  // Stock Keeping Unit (código único)
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.AVAILABLE;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // ============================================================
    // LÓGICA DE NEGOCIO
    // ============================================================
    
    /**
     * Verifica si el producto está disponible para la venta
     */
    public boolean isAvailable() {
        return this.active && 
               this.status == ProductStatus.AVAILABLE && 
               this.stock > 0;
    }
    
    /**
     * Aumenta el stock del producto
     */
    public void increaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.stock += quantity;
        updateStatus();
    }
    
    /**
     * Disminuye el stock del producto
     */
    public void decreaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (this.stock < quantity) {
            throw new IllegalStateException(
                "Stock insuficiente. Disponible: " + this.stock + ", Solicitado: " + quantity
            );
        }
        this.stock -= quantity;
        updateStatus();
    }
    
    /**
     * Actualiza el estado según el stock
     */
    private void updateStatus() {
        if (this.stock == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else if (this.stock > 0 && this.status == ProductStatus.OUT_OF_STOCK) {
            this.status = ProductStatus.AVAILABLE;
        }
    }
    
    /**
     * Marca el producto como descontinuado
     */
    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
        this.active = false;
    }
    
    /**
     * Desactiva el producto
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Activa el producto
     */
    public void activate() {
        this.active = true;
    }
    
    /**
     * Verifica si el producto tiene stock bajo (menos de 10 unidades)
     */
    public boolean hasLowStock() {
        return this.stock > 0 && this.stock < 10;
    }
    
    /**
     * Calcula el precio con descuento
     */
    public BigDecimal getPriceWithDiscount(float discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 1) {
            throw new IllegalArgumentException("Descuento debe estar entre 0 y 1");
        }
        BigDecimal discount = this.price.multiply(BigDecimal.valueOf(discountPercentage));
        return this.price.subtract(discount);
    }
}
