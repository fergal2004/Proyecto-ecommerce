package com.ads.ecommerce.customer.dto;

import java.time.LocalDateTime;

import com.ads.ecommerce.customer.model.CustomerType;
import com.ads.ecommerce.customer.model.TaxIdType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String customerCode;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String taxId;
    private TaxIdType taxIdType;
    private CustomerType customerType;
    private Boolean active;
    private Float discount;
    private LocalDateTime createdAt;
}
