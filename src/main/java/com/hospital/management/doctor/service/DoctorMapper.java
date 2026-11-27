package com.hospital.management.doctor.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hospital.management.doctor.dto.DoctorPatientResponse;
import com.hospital.management.doctor.dto.DoctorResponse;
import com.hospital.management.doctor.model.DoctorDocument;
import com.hospital.management.patient.dto.PatientDoctorResponse;

@Component
public class DoctorMapper {

    public DoctorResponse toResponse(DoctorDocument document, List<PatientDoctorResponse> patient) {
        return new DoctorResponse(
                document.getId(),
                document.getfullName(),
                document.getlicenceNumber(),
                document.getSpeciality(),
                document.getDepartment(),
                patient

        );
    }

    public DoctorPatientResponse toResponse(DoctorDocument document) {
        return new DoctorPatientResponse(document.getId(),
                document.getfullName(),
                document.getlicenceNumber(),
                document.getSpeciality(),
                document.getDepartment());

    }

}
