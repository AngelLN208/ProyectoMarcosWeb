package com.aviva.appointmentsystem.repository;

import com.aviva.appointmentsystem.entity.Notification;
import com.aviva.appointmentsystem.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPatientId(Long patientId);
    List<Notification> findByStatus(NotificationStatus status);
}