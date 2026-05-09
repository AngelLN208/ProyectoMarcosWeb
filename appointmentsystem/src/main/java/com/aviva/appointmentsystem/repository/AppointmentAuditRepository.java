package com.aviva.appointmentsystem.repository;

import com.aviva.appointmentsystem.entity.AppointmentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentAuditRepository extends JpaRepository<AppointmentAudit, Long> {
    List<AppointmentAudit> findByAppointmentId(Long appointmentId);
}