package com.hospital.management.doctor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.hospital.management.doctor.model.DoctorDocument;

import java.util.Optional;

public interface DoctorRepository extends MongoRepository<DoctorDocument, String> {

    Optional<DoctorDocument> findByLicencenumber(String licencenumber);

     boolean existsByLicencenumber(String licencenumber) ;
     boolean existsById(String id);
     
}
