package com.hospital.management.patient.dto;

import com.hospital.management.patient.model.Gender;
import java.time.LocalDate;

public record PatientDoctorResponse(
                String id,
                String fullName,
                String medicalRecordNumber,
                LocalDate dateOfBirth,
                Gender gender,
                String phone,
                String bloodGroup,
                String notes

) {
}
