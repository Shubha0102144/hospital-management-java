package com.hospital.management.patient.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.hospital.management.patient.model.PatientDoctorDocument;

public interface PatientDoctorRespsoitory extends MongoRepository<PatientDoctorDocument, String> {

    Optional<PatientDoctorDocument> findByPatientId(String patientId);

    boolean existsByDoctorId(String doctorId);

    List<PatientDoctorDocument> findByDoctorId(String doctorId);
}
