package com.ads.ecommerce.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ads.ecommerce.order.model.Payment;
import com.ads.ecommerce.order.model.SaleOrder;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findBySaleOrder(SaleOrder saleOrder);
    
    List<Payment> findBySaleOrderId(Long saleOrderId);
}
