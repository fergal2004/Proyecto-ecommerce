package com.ads.ecommerce.customer.controller;

import java.util.List;
import java.util.Map; 

import org.springframework.http.*; 
import org.springframework.util.LinkedMultiValueMap; 
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ads.ecommerce.customer.dto.CustomerRequest;
import com.ads.ecommerce.customer.dto.CustomerResponse;
import com.ads.ecommerce.customer.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers") 
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // Configuración Keycloak (Login)
    private final String KEYCLOAK_URL = "http://127.0.0.1:8091/realms/ecommerce/protocol/openid-connect/token";
    private final String CLIENT_ID = "ecommerce-client";
    private final String CLIENT_SECRET = "2bFmr0dXByGfqS0PxUgX4XUVbXjdiraU"; // Cambiar si es diferente

    // ==========================================
    //  MÉTODOS DE GESTIÓN DE CLIENTES
    // ==========================================

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody @Valid CustomerRequest request) {
        log.info("POST /api/v1/customers");
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔍 BUSQUEDA POR EMAIL
    // Esta ruta debe ir antes o ser específica para que no choque
    @GetMapping("/search")
    public ResponseEntity<CustomerResponse> getCustomerByEmail(@RequestParam("email") String email) {
        log.info("GET /api/v1/customers/search?email={}", email);
        CustomerResponse customer = customerService.findByEmail(email);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    // 🔍 BUSQUEDA POR ID (CORREGIDO CON REGEX)
    // ⚠️ CAMBIO CLAVE AQUÍ: {id:\\d+} obliga a que sea solo números.
    // Esto evita que la palabra "search" entre aquí y cause error 500.
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        log.info("GET /api/v1/customers/{}", id);
        CustomerResponse response = customerService.getCustomer(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers(@RequestParam(required = false) Boolean active) {
        log.info("GET /api/v1/customers?active={}", active);
        List<CustomerResponse> response = active != null && active
                ? customerService.getActiveCustomers()
                : customerService.getAllCustomers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerRequest request) {
        log.info("PUT /api/v1/customers/{}", id);
        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/promote-vip")
    public ResponseEntity<Void> promoteToVip(@PathVariable Long id) {
        log.info("PATCH /api/v1/customers/{}/promote-vip", id);
        customerService.promoteToVip(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable Long id) {
        log.info("PATCH /api/v1/customers/{}/deactivate", id);
        customerService.deactivateCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCustomer(@PathVariable Long id) {
        log.info("PATCH /api/v1/customers/{}/activate", id);
        customerService.activateCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("DELETE /api/v1/customers/{}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    //  MÉTODO DE LOGIN (SIN CAMBIOS, ESTABA BIEN)
    // ==========================================
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("POST /api/v1/customers/login - Intentando acceder: {}", request.getEmail());

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", CLIENT_ID);
            map.add("client_secret", CLIENT_SECRET);
            map.add("username", request.getEmail());
            map.add("password", request.getPassword());
            map.add("grant_type", "password");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                KEYCLOAK_URL, HttpMethod.POST, entity, Map.class
            );

            log.info("✅ Login exitoso para {}", request.getEmail());
            return ResponseEntity.ok(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("❌ Error Keycloak: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("Credenciales incorrectas");
        } catch (Exception e) {
            log.error("❌ Error interno: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;
        
        public LoginRequest() {}
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}