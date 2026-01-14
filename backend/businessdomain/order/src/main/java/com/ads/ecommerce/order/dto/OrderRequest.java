package com.ads.ecommerce.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long customerId;
    
    @NotEmpty(message = "Debe incluir al menos un producto")
    @Valid
    private List<OrderLineRequest> items;
    
    private String notes;
}