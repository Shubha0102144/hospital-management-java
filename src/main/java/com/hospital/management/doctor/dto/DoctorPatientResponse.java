package com.hospital.management.doctor.dto;

public record DoctorPatientResponse(
                String id,
                String fullName,
                String licenceNumber,
                String speciality,
                String department

) {
}
