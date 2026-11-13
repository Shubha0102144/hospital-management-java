package com.hospital.management.doctor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.hospital.management.doctor.model.DoctorDocument;
import java.util.Optional;

public interface DoctorRepository extends MongoRepository<DoctorDocument, String> {

    Optional<DoctorDocument> findByLicenceNumberIgnoreCase(String licenceNumber);

    boolean existsByLicenceNumber(String licenceNumber);

    boolean existsById(String id);

    Optional<DoctorDocument> findByIdIgnoreCase(String id);

}
