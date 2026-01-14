package com.ads.ecommerce.order.model;

public enum OrderStatus {
    PENDING,        // Orden creada, pendiente de pago
    CONFIRMED,      // Pago confirmado
    PROCESSING,     // En proceso de preparación
    SHIPPED,        // Enviada
    DELIVERED,      // Entregada
    CANCELLED       // Cancelada
}