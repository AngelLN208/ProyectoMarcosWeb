package com.aviva.appointmentsystem.service;

import com.aviva.appointmentsystem.entity.Specialty;
import com.aviva.appointmentsystem.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    public List<Specialty> listarTodos() {
        return specialtyRepository.findAll();
    }

    public Optional<Specialty> buscarPorId(Long id) {
        return specialtyRepository.findById(id);
    }
}