package com.ads.ecommerce.customer.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ads.ecommerce.customer.model.Customer;
import com.ads.ecommerce.customer.model.CustomerType;
import com.ads.ecommerce.customer.model.TaxIdType;
import com.ads.ecommerce.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Bean
    CommandLineRunner loadData(CustomerRepository repository) {
        return args -> {
            // Solo cargar si la base de datos está vacía
            if (repository.count() == 0) {
                Customer vipCustomer = new Customer();
                vipCustomer.setName("Juan Pérez");
                vipCustomer.setEmail("juan@vip.com");
                vipCustomer.setCustomerCode("VIP-2024-00001");
                vipCustomer.setTaxId("1712345678");
                vipCustomer.setTaxIdType(TaxIdType.CEDULA);
                vipCustomer.setCustomerType(CustomerType.VIP); // ¡Crucial para el descuento!
                vipCustomer.setAddress("Quito");
                vipCustomer.setActive(true);
                
                repository.save(vipCustomer);
                System.out.println("✅ Cliente VIP de prueba cargado: ID " + vipCustomer.getId());
            }
        };
    }
}