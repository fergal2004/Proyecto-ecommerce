package com.ads.ecommerce.customer.service;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ads.ecommerce.customer.dto.CustomerRequest;
import com.ads.ecommerce.customer.dto.CustomerResponse;
import com.ads.ecommerce.customer.exception.CustomerNotFoundException;
import com.ads.ecommerce.customer.model.Customer;
import com.ads.ecommerce.customer.model.CustomerType;
import com.ads.ecommerce.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

       private final CustomerRepository customerRepository;
    private int counter = 1;
    
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating customer: {}", request.getEmail());
        
        // Validaciones
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }
        
        if (customerRepository.existsByTaxId(request.getTaxId())) {
            throw new IllegalArgumentException("TaxId ya existe");
        }
        
        // Crear customer
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setTaxId(request.getTaxId());
        customer.setTaxIdType(request.getTaxIdType());
        customer.setCustomerType(request.getCustomerType());
        customer.setCustomerCode(generateCode(request.getCustomerType()));
        customer.setActive(true);
        
        Customer saved = customerRepository.save(customer);
        
        return toResponse(saved);
    }
    
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + id));
        
        return toResponse(customer);
    }
    
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CustomerResponse> getActiveCustomers() {
        return customerRepository.findByActive(true).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + id));
        
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        
        Customer updated = customerRepository.save(customer);
        
        return toResponse(updated);
    }
    
    @Transactional
    public void promoteToVip(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + id));
        
        customer.promoteToVip();
        customerRepository.save(customer);
    }
    
    @Transactional
    public void deactivateCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + id));
        
        customer.deactivate();
        customerRepository.save(customer);
    }
    
 @Transactional
    public void activateCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + id));
        
        customer.activate();
        customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
    
    // Métodos auxiliares
      public String generateCode(CustomerType type) {
        String prefix = getPrefix(type);
        String yearPrefix = prefix + "-" + Year.now().getValue();
        
        Long lastSequence = findLastSequenceNumber(yearPrefix);
        Long nextSequence = lastSequence + 1;
        
        return String.format("%s-%05d", yearPrefix, nextSequence);
    }
    
    private String getPrefix(CustomerType type) {
        return switch (type) {
            case VIP -> "VIP";
            case WHOLESALE -> "WHO";
            default -> "REG";
        };
    }
    
    private Long findLastSequenceNumber(String yearPrefix) {
        try {
            Optional<Customer> lastCustomer = customerRepository
                .findTopByCustomerCodeStartingWithOrderByCustomerCodeDesc(yearPrefix);
            
            if (lastCustomer.isPresent()) {
                String lastCode = lastCustomer.get().getCustomerCode(); // Usar getCustomerCode()
                return extractSequenceNumber(lastCode);
            }
            return 0L;
            
        } catch (Exception e) {
            // Log the error and return 0 as fallback
            e.printStackTrace();
            return 0L;
        }
    }
    
    private Long extractSequenceNumber(String code) {
        try {
            if (code != null && code.contains("-")) {
                String sequencePart = code.substring(code.lastIndexOf("-") + 1);
                return Long.parseLong(sequencePart);
            }
        } catch (NumberFormatException e) {
            // Log warning
            System.err.println("Error extrayendo número de secuencia del código: " + code);
        }
        return 0L;
    }
    
    
    private CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setCustomerCode(customer.getCustomerCode());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setAddress(customer.getAddress());
        response.setTaxId(customer.getTaxId());
        response.setTaxIdType(customer.getTaxIdType());
        response.setCustomerType(customer.getCustomerType());
        response.setActive(customer.getActive());
        response.setDiscount(customer.getDiscount());
        response.setCreatedAt(customer.getCreatedAt());
        
        return response;
    }

}
