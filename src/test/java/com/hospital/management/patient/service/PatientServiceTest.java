package com.hospital.management.patient.service;

import com.hospital.management.doctor.dto.DoctorResponse;
import com.hospital.management.doctor.model.DoctorDocument;
import com.hospital.management.doctor.repository.DoctorRepository;
import com.hospital.management.doctor.service.DoctorMapper;
import com.hospital.management.exception.BadRequestException;
import com.hospital.management.exception.ConflictException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.patient.dto.PatientRegisterRequest;
import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.dto.PatientUpdateRequest;
import com.hospital.management.patient.model.Gender;
import com.hospital.management.patient.model.PatientDoctorDocument;
import com.hospital.management.patient.model.PatientDocument;
import com.hospital.management.patient.repository.PatientDoctorRespsoitory;
import com.hospital.management.patient.repository.PatientRepository;
import com.hospital.management.user.model.UserDocument;
import com.hospital.management.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DoctorResponse doctorResponse;
    private PatientService patientService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PatientDoctorRespsoitory patientDoctorRespsoitory;

    @BeforeEach
    void setUp() {
        patientService = new PatientService(patientRepository, new PatientMapper(), doctorRepository, userRepository,
                patientDoctorRespsoitory, new DoctorMapper());
    }

    @Test
    void updateReplacesMutableFields() {
        DoctorDocument doc = new DoctorDocument();
        doc.setId("doc-1");
        PatientDocument existing = new PatientDocument();
        existing.setId("patient-1");
        existing.setFullName("Old Name");
        existing.setMedicalRecordNumber("MRN-100001");
        existing.setGender(Gender.UNKNOWN);

        PatientUpdateRequest request = new PatientUpdateRequest(
                "Ada Lovelace",
                "doc-1",
                "mrn-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "o+",
                "Updated notes");

        when(patientRepository.findById("patient-1")).thenReturn(Optional.of(existing));
        when(doctorRepository.findById("doc-1")).thenReturn(Optional.of(doc));

        when(patientRepository.existsByMedicalRecordNumberAndIdNot("MRN-100002", "patient-1")).thenReturn(false);
        when(patientRepository.save(any(PatientDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response = patientService.update("patient-1", request);

        assertThat(response.fullName()).isEqualTo("Ada Lovelace");
        assertThat(response.medicalRecordNumber()).isEqualTo("MRN-100002");
        assertThat(response.dateOfBirth()).isEqualTo(LocalDate.of(1815, 12, 10));
        assertThat(response.gender()).isEqualTo(Gender.FEMALE);
        assertThat(response.bloodGroup()).isEqualTo("O+");
    }

    @Test
    void updateFailsWhenPatientMissing() {
        when(patientRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.update("missing", Request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateFailsWhenMedicalRecordNumberConflicts() {
        DoctorDocument doc = new DoctorDocument();
        doc.setId("Dr. Grace Hopper");
        PatientDocument existing = new PatientDocument();
        existing.setId("patient-1");
        existing.setPrimaryDoctorId("Dr. Grace Hopper");
        existing.setMedicalRecordNumber("MRN-100001");

        when(patientRepository.findById("patient-1")).thenReturn(Optional.of(existing));
        when(doctorRepository.findById("Dr. Grace Hopper")).thenReturn(Optional.of(doc));

        when(patientRepository.existsByMedicalRecordNumberAndIdNot("MRN-100002", "patient-1")).thenReturn(true);

        assertThatThrownBy(() -> patientService.update("patient-1", Request()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Medical record number");
    }

    @Test
    void updateFailsWhenDateOfBirthIsInTheFuture() {
        DoctorDocument doc = new DoctorDocument();
        doc.setId("Dr. Grace Hopper");

        PatientDocument existing = new PatientDocument();
        existing.setId("patient-1");
        existing.setMedicalRecordNumber("MRN-100001");

        PatientUpdateRequest request = new PatientUpdateRequest(
                "Ada Lovelace",

                "Dr. Grace Hopper",
                "MRN-100002",
                LocalDate.now().plusDays(1),
                Gender.FEMALE,
                null,
                null,
                null);

        when(patientRepository.findById("patient-1")).thenReturn(Optional.of(existing));
        when(doctorRepository.findById("Dr. Grace Hopper")).thenReturn(Optional.of(doc));

        when(patientRepository.existsByMedicalRecordNumberAndIdNot("MRN-100002", "patient-1")).thenReturn(false);

        assertThatThrownBy(() -> patientService.update("patient-1", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("future");
    }

    private PatientRegisterRequest sampleRequest() {
        return new PatientRegisterRequest(
                "Ada Lovelace",
                "doc1",
                "Dr. Grace Hopper",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
    }

    private PatientUpdateRequest Request() {
        return new PatientUpdateRequest(
                "Ada Lovelace",
                "Dr. Grace Hopper",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
    }

    @Test
    void registerAPatient() {

        DoctorDocument doctor = new DoctorDocument();
        doctor.setId("doc-1");
        doctor.setfullName("Dr. Grace Hopper");

        UserDocument user = new UserDocument();
        user.setId("123");

        PatientRegisterRequest request = new PatientRegisterRequest(

                "Ada Lovelace",
                "123",
                "doc-1",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "o+",
                "Updated notes");

        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        when(doctorRepository.findById("doc-1")).thenReturn(Optional.of(doctor));
        when(patientRepository.existsByMedicalRecordNumber("MRN-100002")).thenReturn(false);
        when(patientRepository.save(any(PatientDocument.class))).thenAnswer(invocation -> {
            PatientDocument document = invocation.getArgument(0);
            document.setId("1");
            return document;
        });

        PatientResponse response = patientService.register(request);

        ArgumentCaptor<PatientDocument> captor = ArgumentCaptor.forClass(PatientDocument.class);
        verify(patientRepository).save(captor.capture());

        PatientDocument saved = captor.getValue();
        assertThat(saved.getFullName()).isEqualTo("Ada Lovelace");
        assertThat(saved.getMedicalRecordNumber()).isEqualTo("MRN-100002");
        assertThat(response.fullName()).isEqualTo("Ada Lovelace");
        assertThat(response.medicalRecordNumber()).isEqualTo("MRN-100002");

    }

    @Test
    void registerFailsForDuplicatMedicalNumber() {

        when(patientRepository.existsByMedicalRecordNumber("hsghas")).thenReturn(true);

        assertThatThrownBy(() -> patientService.register(
                new PatientRegisterRequest("Ada Lovelace",
                        "123",
                        "doc-1",
                        "MRN-100002",
                        LocalDate.of(1815, 12, 10),
                        Gender.FEMALE,
                        "+15551230001",
                        "o+",
                        "Updated notes")))

                .hasMessageContaining("already registered");
    }

    @Test
    void patientRegisterFailsForInvalidDoctorId() {
        when(doctorRepository.existsById("doc-1")).thenReturn(false);
        assertThatThrownBy(() -> patientService.register(
                new PatientRegisterRequest("Ada Lovelace",
                        "123",
                        "doc-1",
                        "MRN-100002",
                        LocalDate.of(1815, 12, 10),
                        Gender.FEMALE,
                        "+15551230001",
                        "o+",
                        "Updated notes")))

                .hasMessageContaining("doctor not found");
    }

    @Test
    void ViewRegisteredPatientByValidId() {
        DoctorDocument doctor = new DoctorDocument();
        doctor.setId("doc-1");
        doctor.setfullName("Dr. Grace Hopper");

        UserDocument user = new UserDocument();
        user.setId("123");

        PatientDoctorDocument pat = new PatientDoctorDocument();
        pat.setDoctorId("doc-1");
        pat.setPatientId("1");

        PatientRegisterRequest request = new PatientRegisterRequest(

                "Ada Lovelace",
                "123",
                "doc-1",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "o+",
                "Updated notes");

        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        when(doctorRepository.findById("doc-1")).thenReturn(Optional.of(doctor));
        when(patientDoctorRespsoitory.findByPatientId("1")).thenReturn(Optional.of(pat));

        when(patientRepository.existsByMedicalRecordNumber("MRN-100002")).thenReturn(false);
        when(patientRepository.save(any(PatientDocument.class))).thenAnswer(invocation -> {
            PatientDocument document = invocation.getArgument(0);
            document.setId("1");
            return document;
        });

        PatientResponse response = patientService.register(request);

        ArgumentCaptor<PatientDocument> captor = ArgumentCaptor.forClass(PatientDocument.class);
        verify(patientRepository).save(captor.capture());

        PatientDocument saved = captor.getValue();
        when(patientRepository.findById("1")).thenReturn(Optional.of(saved));

        assertThat(saved.getFullName()).isEqualTo("Ada Lovelace");

        PatientResponse view = patientService.viewPatient("1");
        assertThat(view.fullName()).isEqualTo("Ada Lovelace");
        assertThat(view.medicalRecordNumber()).isEqualTo("MRN-100002");

    }

}
