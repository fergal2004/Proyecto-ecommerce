package com.ads.ecommerce.order.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    
    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Error en llamada Feign: {} - Status: {}", methodKey, response.status());
        
        switch (response.status()) {
            case 400:
                return new IllegalArgumentException("Bad Request al servicio externo: " + methodKey);
            case 404:
                return new RuntimeException("Recurso no encontrado en servicio externo: " + methodKey);
            case 503:
                return new RuntimeException("Servicio no disponible: " + methodKey);
            default:
                return defaultDecoder.decode(methodKey, response);
        }
    }
}