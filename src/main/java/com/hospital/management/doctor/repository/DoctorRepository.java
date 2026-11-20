package com.hospital.management.doctor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.hospital.management.doctor.model.DoctorDocument;
import java.util.Optional;

public interface DoctorRepository extends MongoRepository<DoctorDocument, String> {

    Optional<DoctorDocument> findByLicenceNumberIgnoreCase(String licenceNumber);

    boolean existsByLicenceNumber(String licenceNumber);

    Optional<DoctorDocument> findById(String id);

    boolean existsById(String id);

    Page<DoctorDocument> findByFullNameContainingOrderByFullNameAsc(String fullName, Pageable page);

    boolean existsByFullName(String fullName);

    boolean existsByLicenceNumberAndIdNot(String licenceNumber, String id);

}
