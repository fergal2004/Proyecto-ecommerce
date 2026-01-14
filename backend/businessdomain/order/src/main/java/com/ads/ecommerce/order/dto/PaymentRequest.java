package com.ads.ecommerce.order.dto;

import java.math.BigDecimal;

import com.ads.ecommerce.order.model.PaymentMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;
    
    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;
    
    private String transactionId;
    private String notes;
}
