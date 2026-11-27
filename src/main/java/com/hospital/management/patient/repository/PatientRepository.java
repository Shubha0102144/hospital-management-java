package com.hospital.management.patient.repository;

import com.hospital.management.patient.model.PatientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface PatientRepository extends MongoRepository<PatientDocument, String> {

    Optional<PatientDocument> findByMedicalRecordNumber(String medicalRecordNumber);

    boolean existsByMedicalRecordNumberAndIdNot(String medicalRecordNumber, String id);

    boolean existsByMedicalRecordNumber(String medicalRecordNumber);

    Optional<PatientDocument> findById(String id);

    Optional<PatientDocument> findByPrimaryDoctorId(String primaryDoctorId);
}
