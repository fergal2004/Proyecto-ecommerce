

package com.ads.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String customerCode;
    private String name;
    private String email;
    private Float discount;  // Descuento del cliente (0.0 - 1.0)
    private Boolean active;
}
