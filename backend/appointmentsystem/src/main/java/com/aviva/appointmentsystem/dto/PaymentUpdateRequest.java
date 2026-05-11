package com.aviva.appointmentsystem.dto;

public record PaymentUpdateRequest(
    String status,
    @com.fasterxml.jackson.annotation.JsonAlias({"method", "paymentMethod", "payment_method"})
    String method,
    String reference,
    String description
) {
}
