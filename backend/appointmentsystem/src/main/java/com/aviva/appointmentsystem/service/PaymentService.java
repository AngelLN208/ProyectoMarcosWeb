package com.aviva.appointmentsystem.service;

import com.aviva.appointmentsystem.dto.PaymentResponse;
import com.aviva.appointmentsystem.entity.Appointment;
import com.aviva.appointmentsystem.entity.AppointmentStatus;
import com.aviva.appointmentsystem.entity.Notification;
import com.aviva.appointmentsystem.entity.Payment;
import com.aviva.appointmentsystem.entity.PaymentMethod;
import com.aviva.appointmentsystem.entity.PaymentStatus;
import com.aviva.appointmentsystem.entity.Receipt;
import com.aviva.appointmentsystem.exception.ResourceNotFoundException;
import com.aviva.appointmentsystem.exception.ValidationException;
import com.aviva.appointmentsystem.repository.AppointmentRepository;
import com.aviva.appointmentsystem.repository.PaymentRepository;
import com.aviva.appointmentsystem.repository.ReceiptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestionar pagos
 */
@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Crear nuevo pago manualmente
     */
    public PaymentResponse createPayment(com.aviva.appointmentsystem.dto.PaymentRequest request) {
        logger.info("Creando nuevo pago manualmente para cita ID={}", request.appointmentId());

        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita", request.appointmentId()));

        if (!paymentRepository.findByAppointmentId(request.appointmentId()).isEmpty()) {
            throw new ValidationException("Ya existe un pago pre-registrado para la cita ID=" + request.appointmentId());
        }

        Payment payment = new Payment();
        payment.setAppointment(appointment);
        payment.setAmount(request.amount());

        if (request.method() != null && !request.method().isBlank()) {
            payment.setMethod(PaymentMethod.valueOf(request.method().toUpperCase()));
        } else {
            payment.setMethod(PaymentMethod.CASH);
        }

        if (request.status() != null && !request.status().isBlank()) {
            String s = request.status().toUpperCase();
            if (s.equals("COMPLETED")) s = "PAID";
            payment.setStatus(PaymentStatus.valueOf(s));
        } else {
            payment.setStatus(PaymentStatus.PENDING);
        }

        if (request.reference() != null && !request.reference().isBlank()) {
            payment.setDescription("Referencia: " + request.reference());
        } else {
            payment.setDescription(request.description());
        }

        payment.setCreatedAt(java.time.LocalDateTime.now());
        payment.setUpdatedAt(java.time.LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved);
    }

    /**
     * Actualiza un pago manualmente
     */
    public PaymentResponse updatePayment(Long id, com.aviva.appointmentsystem.dto.PaymentUpdateRequest request) {
        logger.info("Actualizando pago manual ID={}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));

        if (request.status() != null && !request.status().isBlank()) {
            String s = request.status().toUpperCase();
            if (s.equals("COMPLETED")) s = "PAID";
            payment.setStatus(PaymentStatus.valueOf(s));
            if (payment.getStatus() == PaymentStatus.PAID && payment.getPaymentDate() == null) {
                payment.setPaymentDate(java.time.LocalDateTime.now());
            }
        }

        if (request.method() != null && !request.method().isBlank()) {
            payment.setMethod(PaymentMethod.valueOf(request.method().toUpperCase()));
        }

        if (request.reference() != null && !request.reference().isBlank()) {
            payment.setDescription("Referencia: " + request.reference());
        }

        if (request.description() != null && !request.description().isBlank()) {
            payment.setDescription(request.description());
        }

        payment.setUpdatedAt(java.time.LocalDateTime.now());
        Payment updated = paymentRepository.save(payment);
        return mapToResponse(updated);
    }

    /**
     * Elimina un pago
     */
    public void deletePayment(Long id) {
        logger.info("Eliminando pago ID={}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
        paymentRepository.delete(payment);
    }

    /**
     * Procesa un pago
     * POST /api/payments/{appointmentId}/process?method=...
     */
    public PaymentResponse processPayment(Long appointmentId, PaymentMethod method) {
        logger.info("Procesando pago para cita ID={}", appointmentId);

        // Obtener la cita
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", appointmentId));

        // Obtener el pago
        List<Payment> payments = paymentRepository.findByAppointmentId(appointmentId);
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("Pago no encontrado para cita: " + appointmentId);
        }

        Payment payment = payments.get(0);

        // Validar que no esté ya pagado
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new ValidationException("El pago ya ha sido procesado");
        }

        // Actualizar pago
        payment.setStatus(PaymentStatus.PAID);
        payment.setMethod(method);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Pago procesado: ID={}", savedPayment.getId());

        // Actualizar cita a CONFIRMADA
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        logger.info("Cita confirmada: ID={}", appointmentId);

        // Crear notificación de pago recibido
        createPaymentNotification(appointment);

        // Generar comprobante
        generateReceipt(savedPayment);

        return mapToResponse(savedPayment);
    }

    /**
     * Obtiene un pago por ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        logger.debug("Obteniendo pago ID={}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));

        return mapToResponse(payment);
    }

    /**
     * Obtiene pagos de una cita
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getByAppointment(Long appointmentId) {
        logger.debug("Obteniendo pagos de cita ID={}", appointmentId);

        return paymentRepository.findByAppointmentId(appointmentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Lista todos los pagos
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAll() {
        logger.debug("Obteniendo todos los pagos");

        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Lista pagos por estado
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getByStatus(PaymentStatus status) {
        logger.debug("Obteniendo pagos con estado: {}", status);

        return paymentRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Genera un comprobante de pago
     */
    private void generateReceipt(Payment payment) {
        logger.info("Generando comprobante para pago ID={}", payment.getId());

        Receipt receipt = new Receipt();
        receipt.setPayment(payment);
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setDescription("Comprobante de pago por consulta médica");
        receipt.setTotalAmount(payment.getAmount());
        receipt.setCreatedAt(LocalDateTime.now());

        receiptRepository.save(receipt);
        logger.info("Comprobante generado: {}", receipt.getReceiptNumber());
    }

    /**
     * Crea notificación de pago recibido
     */
    private void createPaymentNotification(Appointment appointment) {
        try {
            String message = String.format("Tu pago para la cita del %s ha sido recibido. Gracias.",
                appointment.getAppointmentDateTime().toLocalDate());

            notificationService.createNotification(
                Notification.NotificationType.PAYMENT_RECEIVED,
                appointment.getPatient().getEmail(),
                appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName(),
                appointment,
                "Pago recibido",
                message,
                Notification.NotificationChannel.EMAIL,
                LocalDateTime.now()
            );

            logger.info("Notificación de pago creada para cita: ID={}", appointment.getId());
        } catch (Exception e) {
            logger.error("Error creando notificación de pago para cita: ID={}", appointment.getId(), e);
        }
    }

    /**
     * Genera un número de comprobante único
     */
    private String generateReceiptNumber() {
        return "RCP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Mapea entidad a DTO
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getAmount(),
            payment.getStatus().name(),
            payment.getMethod().name(),
            payment.getDescription(),
            payment.getPaymentDate(),
            payment.getCreatedAt(),
            payment.getUpdatedAt()
        );
    }
}
