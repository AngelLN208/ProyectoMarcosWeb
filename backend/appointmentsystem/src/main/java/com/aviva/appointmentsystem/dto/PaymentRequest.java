package com.aviva.appointmentsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para crear un pago
 */
public record PaymentRequest(
    @com.fasterxml.jackson.annotation.JsonAlias({"appointmentId", "appointment_id"})
    Long appointmentId,

    @NotNull(message = "El monto no puede estar vacío")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    BigDecimal amount,

    @NotBlank(message = "El método de pago no puede estar vacío")
    @com.fasterxml.jackson.annotation.JsonAlias({"method", "paymentMethod", "payment_method"})
    String method,

    String description,
    String status,
    String reference
) {
}
