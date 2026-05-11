package com.aviva.appointmentsystem.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear un doctor
 */
public record DoctorRequest(
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    String firstName,

    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    String lastName,

    @Size(min = 5, max = 20, message = "El número de licencia debe tener entre 5 y 20 caracteres")
    String licenseNumber,

    String phone,

    String email,

    @com.fasterxml.jackson.annotation.JsonAlias("speciality_id")
    Long specialtyId
) {
}
