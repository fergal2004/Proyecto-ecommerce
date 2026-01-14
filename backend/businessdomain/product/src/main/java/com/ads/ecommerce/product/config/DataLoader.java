package com.ads.ecommerce.product.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ads.ecommerce.product.model.Product;
import com.ads.ecommerce.product.model.ProductCategory;
import com.ads.ecommerce.product.model.ProductStatus;
import com.ads.ecommerce.product.repository.ProductRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                // Producto 1: Laptop (Stock limitado para probar validaciones)
                Product p1 = new Product();
                p1.setSku("LAP-001");
                p1.setName("Laptop HP Pavilion");
                p1.setPrice(new BigDecimal("1300.00"));
                p1.setStock(10); // Stock inicial
                p1.setCategory(ProductCategory.ELECTRONICS);
                p1.setStatus(ProductStatus.AVAILABLE);
                p1.setActive(true);
                repository.save(p1);

                // Producto 2: Mouse
                Product p2 = new Product();
                p2.setSku("MOU-001");
                p2.setName("Mouse Logitech");
                p2.setPrice(new BigDecimal("50.00"));
                p2.setStock(20);
                p2.setCategory(ProductCategory.ELECTRONICS);
                p2.setStatus(ProductStatus.AVAILABLE);
                p2.setActive(true);
                repository.save(p2);

                System.out.println("✅ Productos de prueba cargados.");
            }
        };
    }
}