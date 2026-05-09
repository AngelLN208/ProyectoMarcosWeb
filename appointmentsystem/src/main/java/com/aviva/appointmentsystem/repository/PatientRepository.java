package com.aviva.appointmentsystem.repository;

import com.aviva.appointmentsystem.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByDni(String dni);
    List<Patient> findByDniContainingOrUserFirstNameContainingOrUserLastNameContaining(
        String dni, String firstName, String lastName);
}