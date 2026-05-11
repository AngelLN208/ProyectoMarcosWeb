package com.aviva.appointmentsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear/actualizar una especialidad
 */
public record SpecialtyRequest(
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    String name,

    @Size(max = 500, message = "La descripción no debe exceder 500 caracteres")
    String description
) {
}
