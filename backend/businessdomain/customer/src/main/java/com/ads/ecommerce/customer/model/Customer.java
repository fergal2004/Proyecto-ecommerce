package com.ads.ecommerce.customer.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customers")

public class Customer extends Person {
    @Column(unique = true, nullable = false)
    private String customerCode;
    
    @Column(nullable = false)
    private String taxId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaxIdType taxIdType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType = CustomerType.REGULAR;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Lógica de negocio
    public boolean isVip() {
        return this.customerType == CustomerType.VIP;
    }
    
    public void promoteToVip() {
        this.customerType = CustomerType.VIP;
    }
    
    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
    
    public float getDiscount() {
        return switch (this.customerType) {
            case VIP -> 0.15f;
            case WHOLESALE -> 0.10f;
            default -> 0.0f;
        };
    }
}
