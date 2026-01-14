package com.ads.ecommerce.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer quantity;
    
    @NotNull(message = "La operación es obligatoria")
    private StockOperation operation;
    
    public enum StockOperation {
        ADD,        // Agregar stock
        SUBTRACT    // Restar stock
    }
}
