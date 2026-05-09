package com.aviva.appointmentsystem.service;

import com.aviva.appointmentsystem.entity.Appointment;
import com.aviva.appointmentsystem.entity.AppointmentAudit;
import com.aviva.appointmentsystem.entity.AppointmentStatus;
import com.aviva.appointmentsystem.entity.AuditAction;
import com.aviva.appointmentsystem.entity.Notification;
import com.aviva.appointmentsystem.entity.NotificationStatus;
import com.aviva.appointmentsystem.entity.NotificationType;
import com.aviva.appointmentsystem.entity.Payment;
import com.aviva.appointmentsystem.entity.PaymentStatus;
import com.aviva.appointmentsystem.repository.AppointmentAuditRepository;
import com.aviva.appointmentsystem.repository.AppointmentRepository;
import com.aviva.appointmentsystem.repository.NotificationRepository;
import com.aviva.appointmentsystem.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AppointmentAuditRepository auditRepository;

    public List<Appointment> listarTodos() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> buscarPorId(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> buscarPorPaciente(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> buscarPorDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> buscarPorEstado(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    public Appointment registrar(Appointment appointment) {
        // validar disponibilidad
        boolean ocupado = appointmentRepository
            .findByDoctorIdAndAppointmentDateAndAppointmentTime(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
            ).isPresent();

        if (ocupado) {
            throw new RuntimeException("El médico ya tiene una cita en ese horario");
        }

        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setCreatedAt(LocalDateTime.now());
        Appointment saved = appointmentRepository.save(appointment);

        // generar pago automatico
        Payment payment = new Payment();
        payment.setAppointment(saved);
        payment.setAmount(saved.getDoctor().getSpecialty().getPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // generar notificacion
        Notification notification = new Notification();
        notification.setPatient(saved.getPatient());
        notification.setAppointment(saved);
        notification.setType(NotificationType.CREATION);
        notification.setMessage("Su cita ha sido registrada para el " + saved.getAppointmentDate());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        // registrar auditoria
        registrarAuditoria(saved, AuditAction.CREATE, null, AppointmentStatus.PENDING.name());

        return saved;
    }

    public Appointment reprogramar(Long id, Appointment datos) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        String estadoAnterior = appointment.getStatus().name();

        boolean ocupado = appointmentRepository
            .findByDoctorIdAndAppointmentDateAndAppointmentTime(
                appointment.getDoctor().getId(),
                datos.getAppointmentDate(),
                datos.getAppointmentTime()
            ).isPresent();

        if (ocupado) {
            throw new RuntimeException("El médico ya tiene una cita en ese horario");
        }

        appointment.setAppointmentDate(datos.getAppointmentDate());
        appointment.setAppointmentTime(datos.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);

        // notificacion
        Notification notification = new Notification();
        notification.setPatient(saved.getPatient());
        notification.setAppointment(saved);
        notification.setType(NotificationType.RESCHEDULING);
        notification.setMessage("Su cita ha sido reprogramada para el " + saved.getAppointmentDate());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        registrarAuditoria(saved, AuditAction.UPDATE, estadoAnterior, AppointmentStatus.RESCHEDULED.name());

        return saved;
    }

    public Appointment cancelar(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        String estadoAnterior = appointment.getStatus().name();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);

        // notificacion
        Notification notification = new Notification();
        notification.setPatient(saved.getPatient());
        notification.setAppointment(saved);
        notification.setType(NotificationType.CANCELLATION);
        notification.setMessage("Su cita del " + saved.getAppointmentDate() + " ha sido cancelada");
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        registrarAuditoria(saved, AuditAction.CANCEL, estadoAnterior, AppointmentStatus.CANCELLED.name());

        return saved;
    }

    private void registrarAuditoria(Appointment appointment, AuditAction action,
                                     String estadoAnterior, String estadoNuevo) {
        AppointmentAudit audit = new AppointmentAudit();
        audit.setAppointment(appointment);
        audit.setUser(appointment.getCreatedByUser());
        audit.setAction(action);
        audit.setPreviousStatus(estadoAnterior);
        audit.setNewStatus(estadoNuevo);
        audit.setCreatedAt(LocalDateTime.now());
        auditRepository.save(audit);
    }
}