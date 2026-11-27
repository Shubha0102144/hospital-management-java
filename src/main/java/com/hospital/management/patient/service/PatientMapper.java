package com.hospital.management.patient.service;

import com.hospital.management.doctor.dto.DoctorPatientResponse;
import com.hospital.management.patient.dto.PatientDoctorResponse;
import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.model.PatientDocument;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientResponse toResponse(PatientDocument document, DoctorPatientResponse doctor) {
        return new PatientResponse(
                document.getId(),
                document.getFullName(),
                document.getMedicalRecordNumber(),
                document.getDateOfBirth(),
                document.getGender(),
                document.getPhone(),
                document.getBloodGroup(),
                document.getNotes(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                doctor);

    }

    public PatientDoctorResponse toResponse(PatientDocument document) {
        return new PatientDoctorResponse(
                document.getId(),
                document.getFullName(),
                document.getMedicalRecordNumber(),
                document.getDateOfBirth(),
                document.getGender(),
                document.getPhone(),
                document.getBloodGroup(),
                document.getNotes());
    }
}
