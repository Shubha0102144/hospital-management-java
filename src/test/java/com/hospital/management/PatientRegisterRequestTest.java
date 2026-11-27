package com.hospital.management;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospital.management.patient.dto.PatientRegisterRequest;
import com.hospital.management.patient.model.Gender;

public class PatientRegisterRequestTest {
    private static Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void rejectsIfPatientNameIsBlank() {
        PatientRegisterRequest request = new PatientRegisterRequest("",
                "123",
                "Dr. Grace Hopper",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");

        Set<ConstraintViolation<PatientRegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfPatientUserIdIsBlank() {
        PatientRegisterRequest request = new PatientRegisterRequest("Ada Lovelace",
                " ",
                "Dr. Grace Hopper",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
        Set<ConstraintViolation<PatientRegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorPrimaryDoctorIdIsBlank() {
        PatientRegisterRequest request = new PatientRegisterRequest("Ada Lovelace",
                " ",
                "",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
        Set<ConstraintViolation<PatientRegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfPatientMedicalNumberIsBlank() {
        PatientRegisterRequest request = new PatientRegisterRequest("Ada Lovelace",
                " ",
                "Dr. Grace Hopper",
                "",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
        Set<ConstraintViolation<PatientRegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfPatientNameIsAboveRange() {

        String fullName = "a".repeat(161);
        PatientRegisterRequest request = new PatientRegisterRequest(fullName,
                " ",
                "Dr. Grace Hopper",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
        Set<ConstraintViolation<PatientRegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorIdIsAboveRange() {

        String doctorid = "a1".repeat(80);
        PatientRegisterRequest request = new PatientRegisterRequest("fullName",
                " 12",
                doctorid,
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
        Set<ConstraintViolation<PatientRegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfuseridIsAboveRange() {

        String userid = "a1".repeat(81);
        PatientRegisterRequest request = new PatientRegisterRequest("ullName",
                userid,
                "1",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
        Set<ConstraintViolation<PatientRegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfMedicalNumberIsAboveRange() {

        String medi = "a1".repeat(21);
        PatientRegisterRequest request = new PatientRegisterRequest("ullName",
                "userid",
                "1",
                medi,
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
        Set<ConstraintViolation<PatientRegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfLocalDateIsAboveRange() {

        PatientRegisterRequest request = new PatientRegisterRequest("ullName",
                "userid",
                "1",
                "abc",
                LocalDate.of(2035, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes");
        Set<ConstraintViolation<PatientRegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

}
