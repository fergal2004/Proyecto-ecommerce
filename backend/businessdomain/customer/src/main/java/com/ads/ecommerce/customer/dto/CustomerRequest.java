package com.ads.ecommerce.customer.dto;

import com.ads.ecommerce.customer.model.CustomerType;
import com.ads.ecommerce.customer.model.TaxIdType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;   

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CustomerRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    private String phone;
    private String address;
    
    @NotBlank(message = "El taxId es obligatorio")
    private String taxId;
    
    @NotNull(message = "El tipo de taxId es obligatorio")
    private TaxIdType taxIdType;
    
    private CustomerType customerType = CustomerType.REGULAR;

}
