package com.ads.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ads.ecommerce.order.dto.CustomerDTO;

@FeignClient(name = "customer-service",url = "http://localhost:8081",  configuration = com.ads.ecommerce.order.config.FeignConfig.class)
public interface CustomerClient {
    
    @GetMapping("/api/v1/customers/{id}")
    CustomerDTO getCustomer(@PathVariable("id") Long id);

    
}
