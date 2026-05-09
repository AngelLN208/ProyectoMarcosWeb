package com.aviva.appointmentsystem.service;

import com.aviva.appointmentsystem.entity.Appointment;
import com.aviva.appointmentsystem.entity.AppointmentAudit;
import com.aviva.appointmentsystem.entity.AppointmentStatus;
import com.aviva.appointmentsystem.entity.AuditAction;
import com.aviva.appointmentsystem.entity.Payment;
import com.aviva.appointmentsystem.entity.PaymentStatus;
import com.aviva.appointmentsystem.entity.Receipt;
import com.aviva.appointmentsystem.repository.AppointmentAuditRepository;
import com.aviva.appointmentsystem.repository.AppointmentRepository;
import com.aviva.appointmentsystem.repository.PaymentRepository;
import com.aviva.appointmentsystem.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private AppointmentAuditRepository auditRepository;

    public Optional<Payment> buscarPorCita(Long appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId);
    }

    public List<Payment> listarTodos() {
        return paymentRepository.findAll();
    }

    public Payment registrarPago(Long appointmentId, String paymentMethod) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        Appointment appointment = payment.getAppointment();

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("No se puede pagar una cita cancelada");
        }

        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        // confirmar cita
        String estadoAnterior = appointment.getStatus().name();
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        // generar comprobante
        Receipt receipt = new Receipt();
        receipt.setPayment(payment);
        receipt.setType("BOLETA");
        receipt.setReceiptNumber(generarNumeroComprobante());
        receipt.setIssueDate(LocalDateTime.now());
        receipt.setPaymentMethod(paymentMethod);
        receipt.setCreatedAt(LocalDateTime.now());
        receiptRepository.save(receipt);

        // auditoria
        AppointmentAudit audit = new AppointmentAudit();
        audit.setAppointment(appointment);
        audit.setUser(appointment.getCreatedByUser());
        audit.setAction(AuditAction.CONFIRM);
        audit.setPreviousStatus(estadoAnterior);
        audit.setNewStatus(AppointmentStatus.CONFIRMED.name());
        audit.setCreatedAt(LocalDateTime.now());
        auditRepository.save(audit);

        return payment;
    }

    private String generarNumeroComprobante() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "REC-" + timestamp;
    }
}