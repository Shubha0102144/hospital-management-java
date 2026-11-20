package com.hospital.management.patient.service;

import com.hospital.management.exception.BadRequestException;
import com.hospital.management.exception.ConflictException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.dto.UpdatePatientRequest;
import com.hospital.management.patient.model.PatientDocument;
import com.hospital.management.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    public PatientResponse update(String patientId, UpdatePatientRequest request) {
        PatientDocument patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        String medicalRecordNumber = normalizeMedicalRecordNumber(request.medicalRecordNumber());
        if (patientRepository.existsByMedicalRecordNumberAndIdNot(medicalRecordNumber, patientId)) {
            throw new ConflictException("Medical record number is already assigned to another patient");
        }
        if (request.dateOfBirth().isAfter(LocalDate.now())) {
            throw new BadRequestException("Date of birth cannot be in the future");
        }


        patient.setFullName(request.fullName().trim());
        patient.setPrimaryDoctorName(request.primaryDoctorName().trim());
        patient.setMedicalRecordNumber(medicalRecordNumber);
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setPhone(blankToNull(request.phone()));
        patient.setBloodGroup(blankToNull(request.bloodGroup()) == null
                ? null
                : request.bloodGroup().trim().toUpperCase(Locale.ROOT));
        patient.setNotes(blankToNull(request.notes()));

        PatientDocument saved = patientRepository.save(patient);
        return patientMapper.toResponse(saved);
    }

    private String normalizeMedicalRecordNumber(String medicalRecordNumber) {
        return medicalRecordNumber.replace(" ", "").trim().toUpperCase(Locale.ROOT);
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }


    
}
