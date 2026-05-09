package com.aviva.appointmentsystem.repository;

import com.aviva.appointmentsystem.entity.Payment;
import com.aviva.appointmentsystem.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByAppointmentId(Long appointmentId);
    List<Payment> findByStatus(PaymentStatus status);
}