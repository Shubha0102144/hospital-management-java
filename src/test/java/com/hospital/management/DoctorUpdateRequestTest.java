package com.hospital.management;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospital.management.doctor.dto.DoctorRegister;

public class DoctorUpdateRequestTest {
    private static Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void rejectsIfDoctorNameIsBlank() {
        DoctorRegister request = new DoctorRegister("", "dsjd", "jhd", "sjdn");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorLicenceNumberIsBlank() {
        DoctorRegister request = new DoctorRegister("doctor", " ", "jhd", "sjdn");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorSpecialityIsBlank() {
        DoctorRegister request = new DoctorRegister("doctor", "abc123 ", " ", "sjdn");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorDepartmentIsBlank() {
        DoctorRegister request = new DoctorRegister("doctor", "abc123 ", "xyz", " ");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorNameIsAboveRange() {

        String fullName = "a".repeat(161);
        DoctorRegister request = new DoctorRegister(fullName, "abc", "accc", "xyz");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorLicenceNumberIsAboveRange() {

        String licenceNumber = "a1".repeat(21);
        DoctorRegister request = new DoctorRegister("abc", licenceNumber, "accc", "xyz");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorSpecialityIsAboveRange() {

        String speciality = "a2".repeat(41);
        DoctorRegister request = new DoctorRegister("abc", "ab12", speciality, "xyz");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorDepartmentIsAboveRange() {

        String department = "xy".repeat(41);
        DoctorRegister request = new DoctorRegister("bsd", "abc", "accc", department);

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

}
