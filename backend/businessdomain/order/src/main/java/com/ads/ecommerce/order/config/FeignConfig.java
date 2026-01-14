package com.ads.ecommerce.order.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.ads.ecommerce.order.exception.CustomFeignErrorDecoder;

import feign.Logger;
import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfig {
    
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
    
    /**
     * Decodificador de errores personalizado para Feign
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }
    
    /**
     * RestTemplate con balanceo de carga (alternativa a Feign)
     * Opcional: útil si necesitas llamadas HTTP directas
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}