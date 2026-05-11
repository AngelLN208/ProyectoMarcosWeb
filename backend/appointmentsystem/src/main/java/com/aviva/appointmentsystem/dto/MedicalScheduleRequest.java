package com.aviva.appointmentsystem.dto;

/**
 * DTO para crear/actualizar horarios médicos
 */
public record MedicalScheduleRequest(
    Long doctorId,
    java.time.DayOfWeek dayOfWeek,
    java.time.LocalTime startTime,
    java.time.LocalTime endTime,
    Integer appointmentDurationMinutes,
    Integer maxAppointmentsPerDay,
    Boolean available,
    String notes
) {}
