package com.aviva.appointmentsystem.service;

import com.aviva.appointmentsystem.entity.Patient;
import com.aviva.appointmentsystem.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> listarTodos() {
        return patientRepository.findAll();
    }

    public Optional<Patient> buscarPorId(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> buscarPorDni(String dni) {
        return patientRepository.findByDni(dni);
    }

    public Patient registrar(Patient patient) {
        if (patientRepository.findByDni(patient.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe un paciente con ese DNI");
        }
        patient.setCreatedAt(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    public Patient actualizar(Long id, Patient datos) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        patient.setBirthDate(datos.getBirthDate());
        patient.setPhoneNumber(datos.getPhoneNumber());
        patient.setEmail(datos.getEmail());
        patient.setGender(datos.getGender());
        patient.setInsurance(datos.getInsurance());
        return patientRepository.save(patient);
    }
}