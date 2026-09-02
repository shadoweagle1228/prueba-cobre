package com.cobre.notification.infrastructure.adapter.out.persistence;

import com.cobre.notification.domain.model.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SpringDataNotificationRepository extends JpaRepository<NotificationEntity, String> {

    Optional<NotificationEntity> findByEventIdAndClientId(String eventId, String clientId);

    @Query("SELECT e FROM NotificationEntity e WHERE e.clientId = :clientId " +
           "AND (:startDate IS NULL OR e.deliveryDate >= :startDate) " +
           "AND (:endDate IS NULL OR e.deliveryDate <= :endDate) " +
           "AND (:status IS NULL OR e.deliveryStatus = :status)")
    Page<NotificationEntity> findByClientIdWithFilters(
            @Param("clientId") String clientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") DeliveryStatus status,
            Pageable pageable);
}
