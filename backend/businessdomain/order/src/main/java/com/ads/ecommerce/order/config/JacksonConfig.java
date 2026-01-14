package com.ads.ecommerce.order.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Configuración de Jackson para manejar correctamente JSON
 * Soluciona problemas de parsing de números y fechas
 */
@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary
    public JsonMapper objectMapper() {
        return JsonMapper.builder()
                // ✅ CORRECTO: Permite números con ceros iniciales (015.00 -> 15.00)
                // Reemplaza el deprecado ALLOW_NUMERIC_LEADING_ZEROS
                .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS)
                
                // Configuraciones de deserialización
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                
                // Configuraciones de serialización
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                
                // Módulo para fechas Java 8+ (LocalDateTime, LocalDate, etc.)
                .addModule(new JavaTimeModule())
                
                .build();
    }
}