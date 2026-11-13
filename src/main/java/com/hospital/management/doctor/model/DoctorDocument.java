package com.hospital.management.doctor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctors")
public class DoctorDocument {

    @Id
    private String id;

    private String fullName;

    @Indexed(unique = true)
    private String licenceNumber;

    private String specialty;
    private String department;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfullName() {
        return fullName;
    }

    public void setfullName(String fullName) {
        this.fullName = fullName;
    }

    public String getlicenceNumber() {
        return licenceNumber;
    }

    public void setLicencenumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}