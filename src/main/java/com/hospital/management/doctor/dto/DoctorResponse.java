package com.hospital.management.doctor.dto;

public record DoctorResponse(
                String id,
                String fullName,
                String licenceNumber,
                String speciality,
                String department

) {
}
