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
//import com.ads.ecommerce.customer.model.Region; // Asegúrate de tener este modelo
import com.ads.ecommerce.customer.model.TaxIdType;
import com.ads.ecommerce.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    // 1. BÚSQUEDA POR EMAIL
    @Transactional(readOnly = true)
    public CustomerResponse findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(this::toResponse)
                .orElse(null); 
    }

    // 2. CREAR CLIENTE GUEST (Sin Keycloak)
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Iniciando creación de cliente GUEST: {}", request.getEmail());

        // Si el email ya existe, retornar el cliente existente (facilita compras recurrentes)
        Optional<Customer> existing = customerRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            log.info("Cliente existente encontrado con email: {}", request.getEmail());
            return toResponse(existing.get());
        }

        // Validar taxId único
        if (request.getTaxId() != null && customerRepository.existsByTaxId(request.getTaxId())) {
            throw new IllegalArgumentException("El TaxId (Cédula/RUC) ya existe");
        }

        // Crear cliente GUEST
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setTaxId(request.getTaxId());

        // Manejo seguro de Enums
        customer.setTaxIdType(request.getTaxIdType() != null ? request.getTaxIdType() : TaxIdType.CEDULA);
        customer.setCustomerType(request.getCustomerType() != null ? request.getCustomerType() : CustomerType.REGULAR);

        // Generar código, activar y marcar como GUEST
        customer.setCustomerCode(generateCode(customer.getCustomerType()));
        customer.setActive(true);
        customer.setState("GUEST");

        // Guardar
        Customer saved = customerRepository.save(customer);
        log.info("Cliente GUEST guardado con ID: {}", saved.getId());

        return toResponse(saved);
    }

    // --- MÉTODOS CRUD ESTÁNDAR ---

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
        customer.setTaxId(request.getTaxId());
        
        if(request.getTaxIdType() != null) customer.setTaxIdType(request.getTaxIdType());
        
        Customer updated = customerRepository.save(customer);
        return toResponse(updated);
    }

    @Transactional
    public void promoteToVip(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + id));
        customer.promoteToVip();
        customerRepository.save(customer);
    }
    @Transactional
    public void deactivateCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + id));
        customer.deactivate();
        customerRepository.save(customer);
    }
    @Transactional
    public void activateCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Cliente no encontrado: " + id));
        customer.activate();
        customerRepository.save(customer);
    }
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // --- MÉTODOS AUXILIARES ---

    public String generateCode(CustomerType type) {
        String prefix = getPrefix(type);
        String yearPrefix = prefix + "-" + Year.now().getValue();
        Long lastSequence = findLastSequenceNumber(yearPrefix);
        Long nextSequence = lastSequence + 1;
        return String.format("%s-%05d", yearPrefix, nextSequence);
    }
    
    private String getPrefix(CustomerType type) {
        if (type == null) return "REG";
        return switch (type) {
            case VIP -> "VIP";
            case WHOLESALE -> "WHO";
            default -> "REG";
        };
    }
    
    private Long findLastSequenceNumber(String yearPrefix) {
        try {
            Optional<Customer> lastCustomer = customerRepository.findTopByCustomerCodeStartingWithOrderByCustomerCodeDesc(yearPrefix);
            if (lastCustomer.isPresent()) {
                return extractSequenceNumber(lastCustomer.get().getCustomerCode());
            }
            return 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    private Long extractSequenceNumber(String code) {
        try {
            if (code != null && code.contains("-")) {
                return Long.parseLong(code.substring(code.lastIndexOf("-") + 1));
            }
        } catch (NumberFormatException e) { }
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
        response.setState(customer.getState());
        response.setDiscount(customer.getDiscount());
        response.setCreatedAt(customer.getCreatedAt());
        return response;
    }
}