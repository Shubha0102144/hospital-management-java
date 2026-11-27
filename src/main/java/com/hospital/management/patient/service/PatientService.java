package com.hospital.management.patient.service;

import com.hospital.management.doctor.dto.DoctorPatientResponse;
import com.hospital.management.doctor.model.DoctorDocument;
import com.hospital.management.doctor.repository.DoctorRepository;
import com.hospital.management.doctor.service.DoctorMapper;
import com.hospital.management.exception.BadRequestException;
import com.hospital.management.exception.ConflictException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.patient.dto.PatientRegisterRequest;
import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.dto.PatientUpdateRequest;
import com.hospital.management.patient.model.PatientDoctorDocument;
import com.hospital.management.patient.model.PatientDocument;
import com.hospital.management.patient.repository.PatientDoctorRespsoitory;
import com.hospital.management.patient.repository.PatientRepository;
import com.hospital.management.user.model.Role;
import com.hospital.management.user.model.UserDocument;
import com.hospital.management.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PatientDoctorRespsoitory patientDoctorRespsoitory;
    private final DoctorMapper doctorMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper,
            DoctorRepository doctorRepository, UserRepository userRepository,
            PatientDoctorRespsoitory patientDoctorRespsoitory,
            DoctorMapper doctorMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.patientDoctorRespsoitory = patientDoctorRespsoitory;
        this.doctorMapper = doctorMapper;
    }

    public PatientResponse update(String patientId, PatientUpdateRequest request) {

        PatientDocument patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        DoctorDocument doctor = doctorRepository.findById(request.primaryDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("doctor not found: " + request.primaryDoctorId()));

        String medicalRecordNumber = normalizeMedicalRecordNumber(request.medicalRecordNumber());
        if (patientRepository.existsByMedicalRecordNumberAndIdNot(medicalRecordNumber, patientId)) {
            throw new ConflictException("Medical record number is already assigned to another patient");
        }
        if (request.dateOfBirth().isAfter(LocalDate.now())) {
            throw new BadRequestException("Date of birth cannot be in the future");
        }
        DoctorPatientResponse doc = doctorMapper.toResponse(doctor);

        patient.setFullName(request.fullName().trim());
        patient.setMedicalRecordNumber(medicalRecordNumber);
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setPhone(blankToNull(request.phone()));
        patient.setBloodGroup(blankToNull(request.bloodGroup()) == null
                ? null
                : request.bloodGroup().trim().toUpperCase(Locale.ROOT));
        patient.setNotes(blankToNull(request.notes()));
        PatientDocument saved = patientRepository.save(patient);

        PatientDoctorDocument patientdoctor = new PatientDoctorDocument();
        patientdoctor.setPatientId(patient.getId());
        patientdoctor.setDoctorId(request.primaryDoctorId());
        PatientDoctorDocument save = patientDoctorRespsoitory.save(patientdoctor);
        return patientMapper.toResponse(saved, doc);

    }

    public PatientResponse register(PatientRegisterRequest request) {
        UserDocument user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("user not found: " + request.userId()));

        if (!user.getRoles().contains(Role.PATIENT)) {
            throw new BadRequestException("invalid role");
        }
        DoctorDocument doctor = doctorRepository.findById(request.primaryDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("doctor not found: " + request.primaryDoctorId()));

        String medicalRecordNumber = normalizeMedicalRecordNumber(request.medicalRecordNumber());
        if (patientRepository.existsByMedicalRecordNumber(medicalRecordNumber)) {
            throw new ConflictException("medical record number exist");
        }
        DoctorPatientResponse doc = doctorMapper.toResponse(doctor);
        PatientDocument patient = new PatientDocument();
        patient.setFullName(request.fullName().trim());
        patient.setUserId(request.userId());
        patient.setMedicalRecordNumber(request.medicalRecordNumber());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setPhone(request.phone());
        patient.setBloodGroup(request.bloodGroup());
        patient.setNotes(request.notes());
        PatientDocument saved = patientRepository.save(patient);

        PatientDoctorDocument patientdoctor = new PatientDoctorDocument();
        patientdoctor.setPatientId(saved.getId());
        patientdoctor.setDoctorId(request.primaryDoctorId());
        PatientDoctorDocument save = patientDoctorRespsoitory.save(patientdoctor);

        return patientMapper.toResponse(saved, doc);

    }

    public PatientResponse viewPatient(String id) {

        PatientDocument patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("patient not found: " + id));

        PatientDoctorDocument doctorpatient = patientDoctorRespsoitory.findByPatientId(patient.getId())
                .orElseThrow(() -> new ResourceNotFoundException("patient not found: " +
                        id));

        DoctorDocument doc = doctorRepository.findById(doctorpatient.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("doctor not found: " + doctorpatient.getDoctorId()));

        DoctorPatientResponse doctor = doctorMapper.toResponse(doc);
        return patientMapper.toResponse(patient, doctor);
    }

    private String normalizeMedicalRecordNumber(String medicalRecordNumber) {
        return medicalRecordNumber.replace(" ", "").trim().toUpperCase(Locale.ROOT);
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
