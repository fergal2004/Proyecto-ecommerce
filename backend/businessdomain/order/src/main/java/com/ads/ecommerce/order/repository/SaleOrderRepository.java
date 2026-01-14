package com.ads.ecommerce.order.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ads.ecommerce.order.model.OrderStatus;
import com.ads.ecommerce.order.model.SaleOrder;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {
    
    Optional<SaleOrder> findByOrderNumber(String orderNumber);
    
    List<SaleOrder> findByCustomerId(Long customerId);
    
    List<SaleOrder> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM SaleOrder o WHERE o.customerId = :customerId AND o.status = :status")
    List<SaleOrder> findByCustomerIdAndStatus(@Param("customerId") Long customerId, 
                                              @Param("status") OrderStatus status);
    
    @Query("SELECT o FROM SaleOrder o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<SaleOrder> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    boolean existsByOrderNumber(String orderNumber);
}