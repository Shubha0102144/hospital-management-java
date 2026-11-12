package com.hospital.management.doctor.service;

import com.hospital.management.doctor.dto.DoctorRegister;
import com.hospital.management.doctor.dto.DoctorResponse;
import com.hospital.management.doctor.model.DoctorDocument;
import com.hospital.management.doctor.repository.DoctorRepository;
import com.hospital.management.exception.ConflictException;

import java.util.Locale;
import org.springframework.stereotype.Service;

@Service

public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    public DoctorResponse register(DoctorRegister request) {

        
        String normalizedLicenceNumber = normalizeLicence(request.licencenumber());

        if (doctorRepository.existsByLicencenumber(normalizedLicenceNumber)) {
            throw new ConflictException("number is already registered");
        }

        DoctorDocument doctor = new DoctorDocument();

        doctor.setName(request.name().trim());
        doctor.setLicencenumber(request.licencenumber());
        doctor.setSpecialty(request.speciality());
        doctor.setDepartment(request.department());

        DoctorDocument saved = doctorRepository.save(doctor);
        return doctorMapper.toResponse(saved);
    }

    public String normalizeLicence(String licenceNumber) {
        return licenceNumber.trim().toLowerCase(Locale.ROOT);
    }
}