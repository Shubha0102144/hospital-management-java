package com.hospital.management.doctor.dto;

public record DoctorResponse(
        String id,
        String name,
        String licencenumber,
        String speciality,
        String department

) {
}
