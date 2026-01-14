package com.ads.ecommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sale_orders")
public class SaleOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;  // Número de orden único
    
    @Column(nullable = false)
    private Long customerId;  // Referencia al customer-service
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;  // Subtotal sin descuentos
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;  // Descuento aplicado
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;  // Total a pagar
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @OneToMany(mappedBy = "saleOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines = new ArrayList<>();
    
    @OneToMany(mappedBy = "saleOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();
    
    @Column(length = 500)
    private String notes;  // Notas adicionales
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // ============================================================
    // LÓGICA DE NEGOCIO
    // ============================================================
    
    /**
     * Agrega una línea de orden
     */
    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.setSaleOrder(this);
        recalculateTotal();
    }
    
    /**
     * Agrega un pago
     */
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setSaleOrder(this);
        checkIfFullyPaid();
    }
    
    /**
     * Recalcula el total de la orden
     */
    public void recalculateTotal() {
        this.subtotal = orderLines.stream()
            .map(OrderLine::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.total = this.subtotal.subtract(this.discount);
    }
    
    /**
     * Aplica un descuento
     */
    public void applyDiscount(BigDecimal discountAmount) {
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo");
        }
        if (discountAmount.compareTo(this.subtotal) > 0) {
            throw new IllegalArgumentException("El descuento no puede ser mayor al subtotal");
        }
        this.discount = discountAmount;
        this.total = this.subtotal.subtract(this.discount);
    }
    
    /**
     * Obtiene el total pagado
     */
    public BigDecimal getTotalPaid() {
        return payments.stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Verifica si la orden está totalmente pagada
     */
    public boolean isFullyPaid() {
        return getTotalPaid().compareTo(this.total) >= 0;
    }
    
    /**
     * Verifica si está totalmente pagada y actualiza el estado
     */
    private void checkIfFullyPaid() {
        if (isFullyPaid() && this.status == OrderStatus.PENDING) {
            this.status = OrderStatus.CONFIRMED;
        }
    }
    
    /**
     * Confirma la orden (pago completo)
     */
    public void confirm() {
        if (!isFullyPaid()) {
            throw new IllegalStateException("La orden no está totalmente pagada");
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    /**
     * Cancela la orden
     */
    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("No se puede cancelar una orden entregada");
        }
        if (this.status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("No se puede cancelar una orden ya enviada");
        }
        this.status = OrderStatus.CANCELLED;
    }
    
    /**
     * Marca como enviada
     */
    public void ship() {
        if (this.status != OrderStatus.CONFIRMED && this.status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Solo se pueden enviar órdenes confirmadas o en proceso");
        }
        this.status = OrderStatus.SHIPPED;
    }
    
    /**
     * Marca como entregada
     */
    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Solo se pueden entregar órdenes enviadas");
        }
        this.status = OrderStatus.DELIVERED;
    }
}