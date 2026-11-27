package com.hospital.management.doctor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import com.hospital.management.doctor.model.DoctorDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hospital.management.doctor.repository.DoctorRepository;
import com.hospital.management.doctor.service.DoctorMapper;
import com.hospital.management.doctor.service.DoctorService;
import com.hospital.management.exception.ConflictException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.patient.model.Gender;
import com.hospital.management.patient.model.PatientDocument;
import com.hospital.management.patient.repository.PatientDoctorRespsoitory;
import com.hospital.management.patient.repository.PatientRepository;
import com.hospital.management.patient.service.PatientMapper;
import com.hospital.management.doctor.dto.DoctorPatientResponse;
import com.hospital.management.doctor.dto.DoctorRegister;
import com.hospital.management.doctor.dto.DoctorResponse;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    private DoctorService doctorService;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PatientDoctorRespsoitory patientDoctorRespsoitory;

    @BeforeEach
    void setUp() {
        doctorService = new DoctorService(doctorRepository, new DoctorMapper(), patientRepository, new PatientMapper(),
                patientDoctorRespsoitory);
    }

    @Test
    void registerCreatesDoctorWithNormalizedLicenceNumber() {
        DoctorRegister request = new DoctorRegister(

                "abc ",
                "abc",
                "dermatologist",
                "xyz");

        when(doctorRepository.existsByLicenceNumber("abc")).thenReturn(false);
        when(doctorRepository.save(any(DoctorDocument.class))).thenAnswer(invocation -> {
            DoctorDocument document = invocation.getArgument(0);
            document.setId("1");
            return document;
        });

        DoctorPatientResponse response = doctorService.register(request);

        ArgumentCaptor<DoctorDocument> captor = ArgumentCaptor.forClass(DoctorDocument.class);
        verify(doctorRepository).save(captor.capture());

        DoctorDocument saved = captor.getValue();
        assertThat(saved.getlicenceNumber()).isEqualTo("abc");
        assertThat(saved.getfullName()).isEqualTo("abc");
        assertThat(saved.getSpeciality()).isEqualTo("dermatologist");
        assertThat(saved.getDepartment()).isEqualTo("xyz");

        assertThat(response.fullName()).isEqualTo("abc");
        assertThat(response.licenceNumber()).isEqualTo("abc");

    }

    @Test
    void registerRejectsDuplicateLicenceNumber() {
        when(doctorRepository.existsByLicenceNumber("hsghas")).thenReturn(true);

        assertThatThrownBy(() -> doctorService.register(
                new DoctorRegister("abc", "hsghas", "Alex Reader", "dhsgds")))

                .hasMessageContaining("already registered");
    }

    @Test
    void ViewInvalidDoctorId() {
        when(doctorRepository.findById("invalidId")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.viewDoctor("invalidId"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void ViewRegisteredDoctorById() {

        DoctorRegister request = new DoctorRegister(

                "abc ",
                "abc",
                "dermatologist",
                "xyz");

        when(doctorRepository.existsByLicenceNumber("abc")).thenReturn(false);
        when(doctorRepository.save(any(DoctorDocument.class))).thenAnswer(invocation -> {
            DoctorDocument document = invocation.getArgument(0);
            document.setId("1");
            return document;
        });
        doctorService.register(request);

        ArgumentCaptor<DoctorDocument> captor = ArgumentCaptor.forClass(DoctorDocument.class);
        verify(doctorRepository).save(captor.capture());

        DoctorDocument saved = captor.getValue();
        when(doctorRepository.findById("1")).thenReturn(Optional.of(saved));

        DoctorResponse view = doctorService.viewDoctor("1");
        assertThat(view.fullName()).isEqualTo("abc");
        assertThat(view.licenceNumber()).isEqualTo("abc");
        assertThat(view.speciality()).isEqualTo("dermatologist");
        assertThat(view.department()).isEqualTo("xyz");

    }

    @Test
    void searchUnregisteredDoctorDetailsByName() {

        Pageable page = PageRequest.of(0, 1);
        when(doctorRepository.findByFullNameContainingOrderByFullNameAsc("invalidName", page))
                .thenReturn(Page.empty());

        Page<DoctorPatientResponse> doc = doctorService.searchDoctorsByName("invalidName", page);
        assertEquals(doc.getNumberOfElements(), 0);
    }

    @Test
    void searchRegisteredDoctorDetialsByName() {

        Pageable page = PageRequest.of(0, 1);
        DoctorRegister request = new DoctorRegister(

                "abc",
                "abc12",
                "dermatologist",
                "xyz");

        when(doctorRepository.save(any(DoctorDocument.class))).thenAnswer(invocation -> {
            DoctorDocument document = invocation.getArgument(0);
            document.setId("1");
            return document;
        });
        doctorService.register(request);

        ArgumentCaptor<DoctorDocument> captor = ArgumentCaptor.forClass(DoctorDocument.class);
        verify(doctorRepository).save(captor.capture());

        DoctorDocument saved = captor.getValue();

        when(doctorRepository.findByFullNameContainingOrderByFullNameAsc("abc", page))
                .thenReturn(new PageImpl<>(List.of(saved)));

        Page<DoctorPatientResponse> search = doctorService.searchDoctorsByName("abc", page);

        assertEquals(1, search.getTotalPages());
        assertEquals(1, search.getNumberOfElements());
        assertEquals(1, search.getTotalPages());
        assertEquals("abc", search.getContent().get(0).fullName());
        assertEquals("abc12", search.getContent().get(0).licenceNumber());
        assertEquals("dermatologist", search.getContent().get(0).speciality());
        assertEquals("xyz", search.getContent().get(0).department());

    }

    @Test
    void updateReplacesDoctorFields() {

        DoctorDocument doctor = new DoctorDocument();
        doctor.setId("doc-1");
        doctor.setfullName("abc");
        doctor.setLicencenumber("lc12");
        doctor.setSpeciality("health");
        doctor.setDepartment("dept-1");

        DoctorRegister request = new DoctorRegister("xyz", "lc12", "health", "dept-1");
        when(doctorRepository.findById("doc-1")).thenReturn(Optional.of(doctor));
        when(doctorRepository.existsByLicenceNumberAndIdNot("lc12", "doc-1")).thenReturn(false);
        when(doctorRepository.save(any(DoctorDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DoctorPatientResponse response = doctorService.updateDoctorDetails("doc-1", request);

        assertThat(response.fullName()).isEqualTo("xyz");
        assertThat(response.licenceNumber()).isEqualTo("lc12");
        assertThat(response.speciality()).isEqualTo("health");
        assertThat(response.department()).isEqualTo("dept-1");

    }

    @Test
    void updateFailsForUnregisteredDoctor() {
        when(doctorRepository.findById("invalidId")).thenReturn(Optional.empty());
        DoctorRegister request = new DoctorRegister("xyz", "lc12", "health", "dept-1");

        assertThatThrownBy(() -> doctorService.updateDoctorDetails("invalidId", request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateFailssWhenLicenceNumberConflicts() {
        DoctorDocument existing = new DoctorDocument();
        existing.setId("doc1");
        existing.setLicencenumber("lc1");
        existing.setSpeciality("ab");
        existing.setDepartment("xyz");

        when(doctorRepository.findById("doc1")).thenReturn(Optional.of(existing));
        when(doctorRepository.existsByLicenceNumberAndIdNot("lc2", "doc1")).thenReturn(true);

        DoctorRegister request = new DoctorRegister("xyz", "lc2", "health", "dept-1");

        assertThatThrownBy(() -> doctorService.updateDoctorDetails("doc1", request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Licence number is already assigned to another doctor");
    }

    @Test
    void deleteExistingDoctor() {

        DoctorRegister request = new DoctorRegister(

                "abc ",
                "abc12",
                "dermatologist",
                "xyz");

        when(doctorRepository.existsByLicenceNumber("abc12")).thenReturn(false);
        when(doctorRepository.save(any(DoctorDocument.class))).thenAnswer(invocation -> {
            DoctorDocument document = invocation.getArgument(0);
            document.setId("1");
            return document;
        });
        doctorService.register(request);

        ArgumentCaptor<DoctorDocument> captor = ArgumentCaptor.forClass(DoctorDocument.class);
        verify(doctorRepository).save(captor.capture());

        DoctorDocument saved = captor.getValue();
        when(doctorRepository.findById("1")).thenReturn(Optional.of(saved));
        String response = doctorService.deleteADoctor("1");
        assertEquals("abc", response);

    }

    @Test
    void deleteFailsForUnregisteredDoctor() {
        when(doctorRepository.findById("invalidId")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> doctorService.deleteADoctor("invalidId"))
                .isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void updateDoctorNamechangesPrimaryDoctorDoctorNameInPatient() {

        DoctorDocument doctor = new DoctorDocument();
        doctor.setId("doc-1");
        doctor.setfullName("abc");
        doctor.setLicencenumber("lc12");
        doctor.setSpeciality("health");
        doctor.setDepartment("dept-1");

        PatientDocument patient = new PatientDocument();
        patient.setId("patient-1");
        patient.setFullName("Name");
        patient.setMedicalRecordNumber("MRN-100001");
        patient.setGender(Gender.UNKNOWN);

        DoctorRegister request = new DoctorRegister("xyz", "lc12", "health", "dept-1");
        when(doctorRepository.findById("doc-1")).thenReturn(Optional.of(doctor));
        when(doctorRepository.existsByLicenceNumberAndIdNot("lc12", "doc-1")).thenReturn(false);
        when(doctorRepository.save(any(DoctorDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DoctorPatientResponse response = doctorService.updateDoctorDetails("doc-1", request);

        assertThat(response.fullName()).isEqualTo("xyz");
    }

}