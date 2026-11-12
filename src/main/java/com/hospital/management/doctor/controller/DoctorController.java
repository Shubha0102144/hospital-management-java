package com.hospital.management.doctor.controller;

import com.hospital.management.doctor.dto.DoctorRegister;
import com.hospital.management.doctor.dto.DoctorResponse;
import com.hospital.management.doctor.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Doctor", description = "Doctor chart APIs. Only update is implemented as a reference slice.")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping(value = "doctors",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "add a doctor", description = "Fully replaces mutable doctpr fields. Requires ADMIN or existing DOCTOR role.")

    @ApiResponse(responseCode = "200", description = "Patient updated", content = @Content(schema = @Schema(implementation = DoctorResponse.class)))

    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)

    @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content)

    @ApiResponse(responseCode = "403", description = "Insufficient role", content = @Content)

    @ApiResponse(responseCode = "404", description = "Patient not found", content = @Content)

    @ApiResponse(responseCode = "409", description = "Medical record number conflict", content = @Content)

    public DoctorResponse register(@Valid @RequestBody DoctorRegister request) {
        try {
            return doctorService.register(request);
        } catch (Exception e) {
            log.error("Failed to register", e);
            return null;
        }
    }
    @GettMapping(value = "doctors",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR','PATIENT')")
    @Operation(summary = "view a doctor", description = "view doctor details.")
    public DoctorResponse 
}
