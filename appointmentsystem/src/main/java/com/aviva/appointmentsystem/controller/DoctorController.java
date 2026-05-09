package com.aviva.appointmentsystem.controller;

import com.aviva.appointmentsystem.entity.Doctor;
import com.aviva.appointmentsystem.entity.DoctorSchedule;
import com.aviva.appointmentsystem.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    // Listar todos los médicos
    @GetMapping
    public ResponseEntity<List<Doctor>> listar() {
        return ResponseEntity.ok(doctorService.listarTodos());
    }

    // Buscar médico por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return doctorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar médicos por especialidad
    @GetMapping("/specialty/{specialtyId}")
    public ResponseEntity<List<Doctor>> buscarPorEspecialidad(@PathVariable Long specialtyId) {
        return ResponseEntity.ok(doctorService.buscarPorEspecialidad(specialtyId));
    }

    // RF-12: Obtener horarios del médico
    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<DoctorSchedule>> obtenerHorarios(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.obtenerHorarios(id));
    }

    // RF-12: Obtener horarios del médico por día
    @GetMapping("/{id}/schedules/{day}")
    public ResponseEntity<List<DoctorSchedule>> obtenerHorariosPorDia(
            @PathVariable Long id,
            @PathVariable String day) {
        return ResponseEntity.ok(doctorService.obtenerHorariosPorDia(id, day));
    }
}