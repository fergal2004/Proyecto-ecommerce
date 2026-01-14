package com.ads.ecommerce.order.dto;

import com.ads.ecommerce.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private BigDecimal totalPaid;
    private BigDecimal pendingAmount;
    private OrderStatus status;
    private List<OrderLineResponse> items;
    private List<PaymentResponse> payments;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}