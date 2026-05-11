package com.aviva.appointmentsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear un paciente
 */
public record PatientRequest(
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    String firstName,

    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    String lastName,

    String email,

    String phone,

    String dni,

    String address,

    java.time.LocalDate dateOfBirth,

    String gender,

    Boolean active
) {}
