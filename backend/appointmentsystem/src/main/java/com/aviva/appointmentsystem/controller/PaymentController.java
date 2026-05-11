package com.aviva.appointmentsystem.controller;

import com.aviva.appointmentsystem.dto.ApiResponse;
import com.aviva.appointmentsystem.dto.PaymentResponse;
import com.aviva.appointmentsystem.entity.PaymentMethod;
import com.aviva.appointmentsystem.entity.PaymentStatus;
import com.aviva.appointmentsystem.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de pagos
 * Maneja endpoints para gestionar pagos de citas
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    /**
     * Crear un pago manualmente
     * POST /api/payments
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @RequestBody com.aviva.appointmentsystem.dto.PaymentRequest request) {
        logger.info("POST /api/payments - Crear pago manualmente");

        PaymentResponse response = paymentService.createPayment(request);

        return ResponseEntity
                .status(org.springframework.http.HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Pago creado exitosamente"));
    }

    /**
     * Actualizar estado de un pago manualmente
     * PUT /api/payments/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePayment(
            @PathVariable Long id,
            @RequestBody com.aviva.appointmentsystem.dto.PaymentUpdateRequest request) {
        logger.info("PUT /api/payments/{} - Actualizar pago manual", id);

        PaymentResponse response = paymentService.updatePayment(id, request);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Pago actualizado exitosamente"));
    }

    /**
     * Eliminar un pago
     * DELETE /api/payments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable Long id) {
        logger.info("DELETE /api/payments/{} - Eliminar pago", id);

        paymentService.deletePayment(id);

        return ResponseEntity
                .ok(ApiResponse.success(null, "Pago eliminado exitosamente"));
    }

    /**
     * Procesa un pago
     * POST /api/payments/{appointmentId}/process?method=...
     */
    @PostMapping("/{appointmentId}/process")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @PathVariable Long appointmentId,
            @RequestParam String method) {
        logger.info("POST /api/payments/{}/process - Procesar pago", appointmentId);

        PaymentMethod paymentMethod = PaymentMethod.valueOf(method.toUpperCase());
        PaymentResponse response = paymentService.processPayment(appointmentId, paymentMethod);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Pago procesado exitosamente"));
    }

    /**
     * Obtiene un pago por ID
     * GET /api/payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(@PathVariable Long id) {
        logger.info("GET /api/payments/{} - Obtener pago", id);

        PaymentResponse response = paymentService.getById(id);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Pago obtenido"));
    }

    /**
     * Obtiene pagos de una cita
     * GET /api/payments/appointment/{appointmentId}
     */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByAppointment(
            @PathVariable Long appointmentId) {
        logger.info("GET /api/payments/appointment/{} - Obtener pagos de cita", appointmentId);

        List<PaymentResponse> response = paymentService.getByAppointment(appointmentId);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Pagos encontrados: " + response.size()));
    }

    /**
     * Lista todos los pagos
     * GET /api/payments
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAll() {
        logger.info("GET /api/payments - Listar pagos");

        List<PaymentResponse> response = paymentService.getAll();

        return ResponseEntity
                .ok(ApiResponse.success(response, "Pagos obtenidos: " + response.size()));
    }

    /**
     * Lista pagos por estado
     * GET /api/payments/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByStatus(
            @PathVariable String status) {
        logger.info("GET /api/payments/status/{} - Obtener pagos por estado", status);

        PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        List<PaymentResponse> response = paymentService.getByStatus(paymentStatus);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Pagos encontrados: " + response.size()));
    }
}
