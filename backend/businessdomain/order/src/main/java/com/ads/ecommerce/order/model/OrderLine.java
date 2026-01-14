package com.ads.ecommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_lines")
public class OrderLine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "sale_order_id", nullable = false)
    private SaleOrder saleOrder;
    
    @Column(nullable = false)
    private Long productId;  // Referencia al product-service
    
    @Column(nullable = false, length = 200)
    private String productName;  // Snapshot del nombre
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;  // Snapshot del precio
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;  // Total de esta línea
    
    /**
     * Calcula el total de la línea
     */
    public void calculateLineTotal() {
        this.lineTotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
    
    public BigDecimal getLineTotal() {
        if (this.lineTotal == null) {
            calculateLineTotal();
        }
        return this.lineTotal;
    }
}
