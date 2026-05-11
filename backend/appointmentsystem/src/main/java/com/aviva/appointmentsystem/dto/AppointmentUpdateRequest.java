package com.aviva.appointmentsystem.dto;

public record AppointmentUpdateRequest(
    String status,
    String notes
) {
}
