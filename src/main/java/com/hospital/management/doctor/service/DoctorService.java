package com.hospital.management.doctor.service;

import com.hospital.management.doctor.dto.DoctorPatientResponse;
import com.hospital.management.doctor.dto.DoctorRegister;
import com.hospital.management.doctor.dto.DoctorResponse;
import com.hospital.management.doctor.model.DoctorDocument;
import com.hospital.management.doctor.repository.DoctorRepository;
import com.hospital.management.patient.dto.PatientDoctorResponse;
import com.hospital.management.patient.model.PatientDocument;
import com.hospital.management.patient.model.PatientDoctorDocument;
import com.hospital.management.patient.repository.PatientDoctorRespsoitory;
import com.hospital.management.patient.repository.PatientRepository;
import com.hospital.management.patient.service.PatientMapper;
import com.hospital.management.exception.ConflictException;
import com.hospital.management.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service

public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PatientDoctorRespsoitory patientDoctorRespsoitory;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper,
            PatientRepository patientRepository, PatientMapper patientMapper,
            PatientDoctorRespsoitory patientDoctorRespsoitory) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.patientDoctorRespsoitory = patientDoctorRespsoitory;
    }

    public DoctorPatientResponse register(DoctorRegister request) {

        String normalizedLicenceNumber = normalizeLicence(request.licenceNumber());

        if (doctorRepository.existsByLicenceNumber(normalizedLicenceNumber)) {
            throw new ConflictException("number is already registered");
        }

        DoctorDocument doctor = new DoctorDocument();

        doctor.setfullName(request.fullName().trim());
        doctor.setLicencenumber(normalizedLicenceNumber);
        doctor.setSpeciality(request.speciality());
        doctor.setDepartment(request.department());

        DoctorDocument saved = doctorRepository.save(doctor);

        return doctorMapper.toResponse(saved);
    }

    public DoctorResponse viewDoctor(String id) {

        DoctorDocument doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));

        List<PatientDoctorDocument> doc = patientDoctorRespsoitory.findByDoctorId(doctor.getId());

        List<PatientDoctorResponse> patients = new ArrayList<>();

        for (PatientDoctorDocument patient : doc) {
            PatientDocument p = patientRepository.findById(patient.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("patient not found: " + patient.getPatientId()));
            patients.add(patientMapper.toResponse(p));
        }
        return doctorMapper.toResponse(doctor, patients);
    }

    public Page<DoctorPatientResponse> searchDoctorsByName(String fullName, Pageable page) {

        return doctorRepository.findByFullNameContainingOrderByFullNameAsc(fullName, page)
                .map(doctorMapper::toResponse);
    }

    public DoctorPatientResponse updateDoctorDetails(String id, DoctorRegister request) {

        DoctorDocument doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));

        if (request.licenceNumber() != null) {
            String licenceNumber = normalizeLicence(request.licenceNumber());

            if (doctorRepository.existsByLicenceNumberAndIdNot(licenceNumber, id)) {
                throw new ConflictException("Licence number is already assigned to another doctor");
            }
            doctor.setLicencenumber(licenceNumber);
        }

        if (request.fullName() != null) {
            doctor.setfullName(request.fullName().trim());
        }
        if (request.speciality() != null) {
            doctor.setSpeciality(request.speciality().trim());
        }
        if (request.department() != null) {
            doctor.setDepartment(request.department().trim());
        }
        DoctorDocument saved = doctorRepository.save(doctor);

        return doctorMapper.toResponse(saved);
    }

    public String deleteADoctor(String id) {
        DoctorDocument doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));

        List<PatientDoctorDocument> doctorAssigned = patientDoctorRespsoitory.findByDoctorId(id);

        doctorRepository.deleteById(id);
        return doctor.getfullName();
    }

    public String normalizeLicence(String licenceNumber) {
        return licenceNumber.trim().toLowerCase(Locale.ROOT);
    }

}
