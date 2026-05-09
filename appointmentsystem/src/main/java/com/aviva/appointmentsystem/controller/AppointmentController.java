package com.aviva.appointmentsystem.controller;

import com.aviva.appointmentsystem.entity.Appointment;
import com.aviva.appointmentsystem.entity.AppointmentStatus;
import com.aviva.appointmentsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // RF-08: Listar todas las citas
    @GetMapping
    public ResponseEntity<List<Appointment>> listar() {
        return ResponseEntity.ok(appointmentService.listarTodos());
    }

    // Buscar cita por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return appointmentService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // RF-07: Buscar citas por paciente
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> buscarPorPaciente(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.buscarPorPaciente(patientId));
    }

    // RF-07: Buscar citas por doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> buscarPorDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.buscarPorDoctor(doctorId));
    }

    // RF-07: Buscar citas por estado
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Appointment>> buscarPorEstado(@PathVariable AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.buscarPorEstado(status));
    }

    // RF-05: Registrar cita
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Appointment appointment) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(appointmentService.registrar(appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // RF-06: Reprogramar cita
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> reprogramar(@PathVariable Long id, @RequestBody Appointment appointment) {
        try {
            return ResponseEntity.ok(appointmentService.reprogramar(id, appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // RF-09: Cancelar cita
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(appointmentService.cancelar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}