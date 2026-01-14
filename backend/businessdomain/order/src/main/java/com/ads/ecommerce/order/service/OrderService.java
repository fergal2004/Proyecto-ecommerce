package com.ads.ecommerce.order.service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ads.ecommerce.order.client.CustomerClient;
import com.ads.ecommerce.order.client.ProductClient;
import com.ads.ecommerce.order.dto.CustomerDTO;
import com.ads.ecommerce.order.dto.OrderLineRequest;
import com.ads.ecommerce.order.dto.OrderLineResponse;
import com.ads.ecommerce.order.dto.OrderRequest;
import com.ads.ecommerce.order.dto.OrderResponse;
import com.ads.ecommerce.order.dto.PaymentRequest;
import com.ads.ecommerce.order.dto.PaymentResponse;
import com.ads.ecommerce.order.dto.ProductDTO;
import com.ads.ecommerce.order.exception.OrderNotFoundException;
import com.ads.ecommerce.order.model.OrderLine;
import com.ads.ecommerce.order.model.OrderStatus;
import com.ads.ecommerce.order.model.Payment;
import com.ads.ecommerce.order.model.SaleOrder;
import com.ads.ecommerce.order.repository.PaymentRepository;
import com.ads.ecommerce.order.repository.SaleOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final SaleOrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    
    private final AtomicInteger orderCounter = new AtomicInteger(1);
    
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        
        // 1. Validar que el cliente existe y está activo
        CustomerDTO customer = customerClient.getCustomer(request.getCustomerId());
        if (!customer.getActive()) {
            throw new IllegalArgumentException("El cliente no está activo");
        }
        
        // 2. Crear la orden
        SaleOrder order = new SaleOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerId(request.getCustomerId());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);
        
        // 3. Procesar cada ítem
        for (OrderLineRequest itemRequest : request.getItems()) {
            // Validar producto
            ProductDTO product = productClient.getProduct(itemRequest.getProductId());
            
            if (!product.getAvailable()) {
                throw new IllegalArgumentException("Producto no disponible: " + product.getName());
            }
            
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para: " + product.getName() + 
                    ". Disponible: " + product.getStock()
                );
            }
            
            // Crear línea de orden
            OrderLine orderLine = new OrderLine();
            orderLine.setProductId(product.getId());
            orderLine.setProductName(product.getName());
            orderLine.setUnitPrice(product.getPrice());
            orderLine.setQuantity(itemRequest.getQuantity());
            orderLine.calculateLineTotal();
            
            order.addOrderLine(orderLine);
            
            // Disminuir stock del producto
            Map<String, Object> stockUpdate = new HashMap<>();
            stockUpdate.put("quantity", itemRequest.getQuantity());
            stockUpdate.put("operation", "SUBTRACT");
            productClient.updateStock(product.getId(), stockUpdate);
        }
        
        // 4. Calcular totales y aplicar descuento del cliente
        order.recalculateTotal();
        if (customer.getDiscount() != null && customer.getDiscount() > 0) {
            BigDecimal discountAmount = order.getSubtotal()
                .multiply(BigDecimal.valueOf(customer.getDiscount()));
            order.applyDiscount(discountAmount);
        }
        
        // 5. Guardar
        SaleOrder saved = orderRepository.save(order);
        log.info("Order created: {} for customer: {}", saved.getOrderNumber(), customer.getName());
        
        return toResponse(saved, customer.getName());
    }
    
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        SaleOrder order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + id));
        
        CustomerDTO customer = customerClient.getCustomer(order.getCustomerId());
        return toResponse(order, customer.getName());
    }
    
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        SaleOrder order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + orderNumber));
        
        CustomerDTO customer = customerClient.getCustomer(order.getCustomerId());
        return toResponse(order, customer.getName());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(order -> {
                CustomerDTO customer = customerClient.getCustomer(order.getCustomerId());
                return toResponse(order, customer.getName());
            })
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
            .map(order -> {
                CustomerDTO customer = customerClient.getCustomer(order.getCustomerId());
                return toResponse(order, customer.getName());
            })
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
            .map(order -> {
                CustomerDTO customer = customerClient.getCustomer(order.getCustomerId());
                return toResponse(order, customer.getName());
            })
            .collect(Collectors.toList());
    }
    
    @Transactional
    public OrderResponse addPayment(Long orderId, PaymentRequest request) {
        log.info("Adding payment to order: {}", orderId);
        
        SaleOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + orderId));
        
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("No se puede pagar una orden cancelada");
        }
        
        // Crear pago
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(request.getTransactionId());
        payment.setNotes(request.getNotes());
        
        order.addPayment(payment);
        
        SaleOrder updated = orderRepository.save(order);
        log.info("Payment added. Total paid: {}/{}", order.getTotalPaid(), order.getTotal());
        
        CustomerDTO customer = customerClient.getCustomer(order.getCustomerId());
        return toResponse(updated, customer.getName());
    }
    
    @Transactional
    public void confirmOrder(Long id) {
        SaleOrder order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + id));
        
        order.confirm();
        orderRepository.save(order);
        log.info("Order confirmed: {}", order.getOrderNumber());
    }
    
    @Transactional
    public void cancelOrder(Long id) {
        SaleOrder order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + id));
        
        // Devolver stock
        for (OrderLine line : order.getOrderLines()) {
            Map<String, Object> stockUpdate = new HashMap<>();
            stockUpdate.put("quantity", line.getQuantity());
            stockUpdate.put("operation", "ADD");
            productClient.updateStock(line.getProductId(), stockUpdate);
        }
        
        order.cancel();
        orderRepository.save(order);
        log.info("Order cancelled: {}", order.getOrderNumber());
    }
    
    @Transactional
    public void shipOrder(Long id) {
        SaleOrder order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + id));
        
        order.ship();
        orderRepository.save(order);
        log.info("Order shipped: {}", order.getOrderNumber());
    }
    
    @Transactional
    public void deliverOrder(Long id) {
        SaleOrder order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + id));
        
        order.deliver();
        orderRepository.save(order);
        log.info("Order delivered: {}", order.getOrderNumber());
    }
    
    // Métodos auxiliares
    private String generateOrderNumber() {
        String year = String.valueOf(Year.now().getValue());
        String sequence = String.format("%06d", orderCounter.getAndIncrement());
        return "ORD-" + year + "-" + sequence;
    }
    
private OrderResponse toResponse(SaleOrder order, String customerName) {
    List<OrderLineResponse> items = order.getOrderLines().stream()
        .map(line -> OrderLineResponse.builder()
            .id(line.getId())
            .productId(line.getProductId())
            .productName(line.getProductName())
            .unitPrice(line.getUnitPrice())
            .quantity(line.getQuantity())
            .lineTotal(line.getLineTotal())
            .build())
        .collect(Collectors.toList());
    
    List<PaymentResponse> payments = order.getPayments().stream()
        .map(payment -> PaymentResponse.builder()
            .id(payment.getId())
            .amount(payment.getAmount())
            .paymentMethod(payment.getPaymentMethod())
            .transactionId(payment.getTransactionId())
            .paymentDate(payment.getPaymentDate())
            .notes(payment.getNotes())
            .build())
        .collect(Collectors.toList());
    
    BigDecimal pendingAmount = order.getTotal().subtract(order.getTotalPaid());
    
    return OrderResponse.builder()
        .id(order.getId())
        .orderNumber(order.getOrderNumber())
        .customerId(order.getCustomerId())
        .customerName(customerName)
        .subtotal(order.getSubtotal())
        .discount(order.getDiscount())
        .total(order.getTotal())
        .totalPaid(order.getTotalPaid())
        .pendingAmount(pendingAmount)
        .status(order.getStatus())
        .items(items)
        .payments(payments)
        .notes(order.getNotes())
        .createdAt(order.getCreatedAt())
        .updatedAt(order.getUpdatedAt())
        .build();

    }
}
