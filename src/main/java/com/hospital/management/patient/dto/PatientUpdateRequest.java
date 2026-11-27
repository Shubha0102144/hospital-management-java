package com.hospital.management.patient.dto;

import java.time.LocalDate;

import com.hospital.management.patient.model.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientUpdateRequest(

                @NotBlank(message = "Full name is required") @Size(min = 2, max = 160, message = "Full name must be between 2 and 160 characters") String fullName,

                @NotBlank(message = "Primary doctor id is required") @Size(min = 1, max = 100, message = "Primary doctor id must be between 1 and 100 characters") String primaryDoctorId,

                @NotBlank(message = "Medical record number is required") @Pattern(regexp = "(?i)^MRN-\\d{6}$", message = "Medical record number must look like MRN-100001") String medicalRecordNumber,

                @NotNull(message = "Date of birth is required") @PastOrPresent(message = "Date of birth cannot be in the future") LocalDate dateOfBirth,

                @NotNull(message = "Gender is required") Gender gender,

                @Pattern(regexp = "^$|^[0-9+() .\\-]{7,20}$", message = "Phone must be between 7 and 20 characters") String phone,

                @Pattern(regexp = "(?i)^(|(A|B|AB|O)[+-])$", message = "Blood group must be one of A+, A-, B+, B-, AB+, AB-, O+, O-") String bloodGroup,

                @Size(max = 2000, message = "Notes must be at most 2000 characters") String notes) {
}
