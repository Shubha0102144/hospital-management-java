package com.hospital.management.doctor.dto;

public record DoctorResponse(
        int id,
        String name,
        String licencenumber,
        String speciality,
        String department

) {
}
