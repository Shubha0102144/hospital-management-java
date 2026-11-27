package com.hospital.management.migration;

import com.hospital.management.config.SeedProperties;
import com.hospital.management.patient.model.Gender;
import com.hospital.management.patient.model.PatientDocument;
import com.hospital.management.user.model.Role;
import com.hospital.management.user.model.UserDocument;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@ChangeUnit(id = "v002-seed-admin-and-patients", order = "002", author = "hospital-starter")
public class V002SeedDataChangelog {

    @Execution
    public void execution(
            MongoTemplate mongoTemplate,
            PasswordEncoder passwordEncoder,
            SeedProperties seedProperties
    ) {
        seedAdmin(mongoTemplate, passwordEncoder, seedProperties);
        seedPatients(mongoTemplate);
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate, SeedProperties seedProperties) {
        mongoTemplate.remove(Query.query(Criteria.where("email").is(seedProperties.adminEmail())), UserDocument.class);
        mongoTemplate.remove(Query.query(Criteria.where("medicalRecordNumber").in(
                "MRN-100001",
                "MRN-100002",
                "MRN-100003"
        )), PatientDocument.class);
    }

    private void seedAdmin(
            MongoTemplate mongoTemplate,
            PasswordEncoder passwordEncoder,
            SeedProperties seedProperties
    ) {
        Query existingAdmin = Query.query(Criteria.where("email").is(seedProperties.adminEmail().toLowerCase()));
        if (mongoTemplate.exists(existingAdmin, UserDocument.class)) {
            return;
        }

        Instant now = Instant.now();
        UserDocument admin = new UserDocument();
        admin.setEmail(seedProperties.adminEmail().toLowerCase());
        admin.setPasswordHash(passwordEncoder.encode(seedProperties.adminPassword()));
        admin.setFullName(seedProperties.adminFullName());
        admin.setRoles(Set.of(Role.ADMIN));
        admin.setEnabled(true);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);
        mongoTemplate.insert(admin);
    }

    private void seedPatients(MongoTemplate mongoTemplate) {
        List<PatientDocument> samples = List.of(
                patient("Ada Lovelace", "Dr. Grace Hopper", "MRN-100001",
                        LocalDate.of(1815, 12, 10), Gender.FEMALE, "+15551230001", "O+",
                        "Reference patient used by the starter seed."),
                patient("Alan Turing", "Dr. Florence Nightingale", "MRN-100002",
                        LocalDate.of(1912, 6, 23), Gender.MALE, null, "AB+",
                        "Reference patient used by the starter seed."),
                patient("Katherine Johnson", "Dr. Jonas Salk", "MRN-100003",
                        LocalDate.of(1918, 8, 26), Gender.FEMALE, null, "A-",
                        "Reference patient used by the starter seed.")
        );

        for (PatientDocument sample : samples) {
            Query byRecord = Query.query(Criteria.where("medicalRecordNumber").is(sample.getMedicalRecordNumber()));
            if (!mongoTemplate.exists(byRecord, PatientDocument.class)) {
                mongoTemplate.insert(sample);
            }
        }
    }

    private PatientDocument patient(
            String fullName,
            String primaryDoctorName,
            String medicalRecordNumber,
            LocalDate dateOfBirth,
            Gender gender,
            String phone,
            String bloodGroup,
            String notes
    ) {
        Instant now = Instant.now();
        PatientDocument document = new PatientDocument();
        document.setFullName(fullName);
        document.setMedicalRecordNumber(medicalRecordNumber);
        document.setDateOfBirth(dateOfBirth);
        document.setGender(gender);
        document.setPhone(phone);
        document.setBloodGroup(bloodGroup);
        document.setNotes(notes);
        document.setCreatedAt(now);
        document.setUpdatedAt(now);
        return document;
    }
}
