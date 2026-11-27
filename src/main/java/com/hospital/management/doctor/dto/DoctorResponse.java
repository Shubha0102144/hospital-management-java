package com.hospital.management.doctor.dto;

import java.util.List;

import com.hospital.management.patient.dto.PatientDoctorResponse;

public record DoctorResponse(
        String id,
        String fullName,
        String licenceNumber,
        String speciality,
        String department,
        List<PatientDoctorResponse> patient

) {
}
