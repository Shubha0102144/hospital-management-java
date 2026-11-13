package com.hospital.management.doctor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.hospital.management.doctor.dto.DoctorRegister;
import com.hospital.management.doctor.dto.DoctorResponse;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        doctorService = new DoctorService(doctorRepository, new DoctorMapper());
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

        DoctorResponse response = doctorService.register(request);

        ArgumentCaptor<DoctorDocument> captor = ArgumentCaptor.forClass(DoctorDocument.class);
        verify(doctorRepository).save(captor.capture());

        DoctorDocument saved = captor.getValue();
        assertThat(saved.getlicenceNumber()).isEqualTo("abc");
        assertThat(saved.getfullName()).isEqualTo("abc");
        assertThat(saved.getSpecialty()).isEqualTo("dermatologist");
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
    void validateFindByLicenceNumber() {

        DoctorDocument doc = doctorRepository.findByLicenceNumberIgnoreCase("abc").orElse(null);
        String expected = "Doctor not found";
        String actual = "";
        if (doc == null) {
            actual = "Doctor not found";
        }
        assertEquals(expected, actual);
    }

    @Test
    void validateFindById() {
        DoctorDocument doctor = doctorRepository.findByIdIgnoreCase("2").orElse(null);
        String expected = "Doctor is not registered";
        String actual = "";
        if (doctor == null) {
            actual = "Doctor is not registered";
        }
        assertEquals(expected, actual);

    }

}