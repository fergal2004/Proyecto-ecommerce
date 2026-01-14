package com.ads.ecommerce.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ads.ecommerce.order.dto.OrderRequest;
import com.ads.ecommerce.order.dto.OrderResponse;
import com.ads.ecommerce.order.dto.PaymentRequest;
import com.ads.ecommerce.order.model.OrderStatus;
import com.ads.ecommerce.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid OrderRequest request) {
        log.info("POST /api/v1/orders");
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        log.info("GET /api/v1/orders/{}", id);
        OrderResponse response = orderService.getOrder(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        log.info("GET /api/v1/orders/order-number/{}", orderNumber);
        OrderResponse response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("GET /api/v1/orders");
        List<OrderResponse> response = orderService.getAllOrders();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(
            @PathVariable Long customerId) {
        log.info("GET /api/v1/orders/customer/{}", customerId);
        List<OrderResponse> response = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @PathVariable OrderStatus status) {
        log.info("GET /api/v1/orders/status/{}", status);
        List<OrderResponse> response = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/payments")
    public ResponseEntity<OrderResponse> addPayment(
            @PathVariable Long id,
            @RequestBody @Valid PaymentRequest request) {
        log.info("POST /api/v1/orders/{}/payments", id);
        OrderResponse response = orderService.addPayment(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable Long id) {
        log.info("PATCH /api/v1/orders/{}/confirm", id);
        orderService.confirmOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        log.info("PATCH /api/v1/orders/{}/cancel", id);
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/ship")
    public ResponseEntity<Void> shipOrder(@PathVariable Long id) {
        log.info("PATCH /api/v1/orders/{}/ship", id);
        orderService.shipOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/deliver")
    public ResponseEntity<Void> deliverOrder(@PathVariable Long id) {
        log.info("PATCH /api/v1/orders/{}/deliver", id);
        orderService.deliverOrder(id);
        return ResponseEntity.noContent().build();
    }
}
