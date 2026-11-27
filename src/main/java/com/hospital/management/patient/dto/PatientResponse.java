package com.hospital.management.patient.dto;

import com.hospital.management.patient.model.Gender;
import com.hospital.management.doctor.dto.DoctorPatientResponse;
import java.time.Instant;
import java.time.LocalDate;

public record PatientResponse(
                String id,
                String fullName,
                String medicalRecordNumber,
                LocalDate dateOfBirth,
                Gender gender,
                String phone,
                String bloodGroup,
                String notes,
                Instant createdAt,
                Instant updatedAt,
                DoctorPatientResponse doctor) {
}
