package com.hospital.management.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoctorRegister(
                 
                @NotBlank(message = "name is required") 
                @Size(min=1,max = 160, message = "name must be at most 200 characters") 
                String fullName,
                
                @NotBlank(message = "licence number is required") 
                @Size(min = 1, max = 40, message = "licence number must be between 1 and 40 characters") 
                String licenceNumber,

                @NotBlank(message = "speciality is required") 
                @Size(min = 1, max = 80, message = "specilaity must be between 1 and 80 characters") 
                String speciality,

                @NotBlank(message = "department is required") 
                @Size(min = 1, max = 80, message = "department must be between 1 and 80 characters") 
                String department

) {
}
