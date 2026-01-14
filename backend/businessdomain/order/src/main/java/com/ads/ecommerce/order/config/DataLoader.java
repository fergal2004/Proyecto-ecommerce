package com.ads.ecommerce.order.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.ads.ecommerce.order.model.OrderLine;
import com.ads.ecommerce.order.model.OrderStatus;
import com.ads.ecommerce.order.model.SaleOrder;
import com.ads.ecommerce.order.repository.SaleOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataLoader {
    
    @Bean
    @Profile("dev")
    CommandLineRunner loadTestData(SaleOrderRepository orderRepository) {
        return args -> {
            if (orderRepository.count() == 0) {
                log.info("Loading test orders...");
                
                // Orden de ejemplo 1
                SaleOrder order1 = new SaleOrder();
                order1.setOrderNumber("ORD-2025-000001");
                order1.setCustomerId(1L);
                order1.setSubtotal(new BigDecimal("150.00"));
                order1.setDiscount(new BigDecimal("22.50"));
                order1.setTotal(new BigDecimal("127.50"));
                order1.setStatus(OrderStatus.PENDING);
                order1.setNotes("Orden de prueba");
                
                OrderLine line1 = new OrderLine();
                line1.setProductId(1L);
                line1.setProductName("Laptop HP");
                line1.setUnitPrice(new BigDecimal("50.00"));
                line1.setQuantity(2);
                line1.calculateLineTotal();
                order1.addOrderLine(line1);
                
                OrderLine line2 = new OrderLine();
                line2.setProductId(2L);
                line2.setProductName("Mouse Logitech");
                line2.setUnitPrice(new BigDecimal("50.00"));
                line2.setQuantity(1);
                line2.calculateLineTotal();
                order1.addOrderLine(line2);
                
                orderRepository.save(order1);
                
                log.info("Test orders loaded successfully");
            }
        };
    }
}
