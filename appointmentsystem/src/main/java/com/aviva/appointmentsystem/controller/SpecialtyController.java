package com.aviva.appointmentsystem.controller;

import com.aviva.appointmentsystem.entity.Specialty;
import com.aviva.appointmentsystem.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@CrossOrigin(origins = "*")
public class SpecialtyController {

    @Autowired
    private SpecialtyService specialtyService;

    // Listar todas las especialidades
    @GetMapping
    public ResponseEntity<List<Specialty>> listar() {
        return ResponseEntity.ok(specialtyService.listarTodos());
    }

    // Buscar especialidad por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return specialtyService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}