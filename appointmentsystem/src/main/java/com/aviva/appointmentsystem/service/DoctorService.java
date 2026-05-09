package com.aviva.appointmentsystem.service;

import com.aviva.appointmentsystem.entity.Doctor;
import com.aviva.appointmentsystem.entity.DoctorSchedule;
import com.aviva.appointmentsystem.repository.DoctorRepository;
import com.aviva.appointmentsystem.repository.DoctorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    public List<Doctor> listarTodos() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> buscarPorId(Long id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> buscarPorEspecialidad(Long specialtyId) {
        return doctorRepository.findBySpecialtyId(specialtyId);
    }

    public List<DoctorSchedule> obtenerHorarios(Long doctorId) {
        return doctorScheduleRepository.findByDoctorId(doctorId);
    }

    public List<DoctorSchedule> obtenerHorariosPorDia(Long doctorId, String day) {
        return doctorScheduleRepository.findByDoctorIdAndDay(doctorId, day);
    }
}