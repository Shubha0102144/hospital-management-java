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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @PostMapping(value = "/doctors", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "add a doctor", description = "Fully replaces mutable doctpr fields. Requires ADMIN or existing DOCTOR role.")

    @ApiResponse(responseCode = "200", description = "doctor registered", content = @Content(schema = @Schema(implementation = DoctorResponse.class)))

    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)

    @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content)

    @ApiResponse(responseCode = "404", description = "Doctor not found", content = @Content)

    @ApiResponse(responseCode = "409", description = "Licence number conflict", content = @Content)

    public DoctorResponse register(@Valid @RequestBody DoctorRegister request) {

        return doctorService.register(request);

    }

    @GetMapping(value = "/doctors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR','PATIENT')")
    @Operation(summary = "view a doctor", description = "Displays doctor information. Requires ADMIN or existing DOCTOR or signed USER(PATIENT) role.")
    @ApiResponse(responseCode = "200", description = "Doctor  found", content = @Content(schema = @Schema(implementation = DoctorResponse.class)))

    @ApiResponse(responseCode = "403", description = "Is unauthorized", content = @Content)

    @ApiResponse(responseCode = "404", description = "Doctor not found", content = @Content)

    public DoctorResponse viewDoctor(@PathVariable String id) throws Exception {
        return doctorService.viewDoctor(id);
    }

    @GetMapping(value = "/doctors/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR','PATIENT')")
    @Operation(summary = "search doctors", description = "Displays doctors information. Requires ADMIN or existing DOCTOR or signed USER(PATIENT) role.")
    @ApiResponse(responseCode = "200", description = "Doctor name found and details displayed", content = @Content(schema = @Schema(implementation = DoctorResponse.class)))

    @ApiResponse(responseCode = "403", description = "Is unauthorized", content = @Content)

    public Page<DoctorResponse> searchDoctorsByName(@RequestParam("fullName") String fullName,
            @RequestParam("page") int page,
            @RequestParam("sizePerPage") int sizePerPage) {
        Pageable pageable = PageRequest.of(page, sizePerPage);
        return doctorService.searchDoctorsByName(fullName, pageable);
    }

    @PutMapping(value = "/doctors/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "update a doctor", description = "Update doctor details. Requires ADMIN or existing DOCTOR role.")
    @ApiResponse(responseCode = "200", description = "Doctor details updated", content = @Content(schema = @Schema(implementation = DoctorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    @ApiResponse(responseCode = "403", description = "Insufficient role", content = @Content)
    @ApiResponse(responseCode = "404", description = "Doctor not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Licence number conflict", content = @Content)

    public DoctorResponse updateDoctorDetails(@PathVariable("id") String id,
            @RequestBody DoctorRegister request) {
        return doctorService.updateDoctorDetails(id, request);

    }
}
