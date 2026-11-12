package com.hospital.management.doctor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctors")
public class DoctorDocument {

    @Id
    private String id;

    private String name;
    
    @Indexed(unique = true)
    private String licencenumber;
    
    private String specialty;
    private String department;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicencenumber() {
        return licencenumber;
    }

    public void setLicencenumber(String licencenumber) {
        this.licencenumber = licencenumber;
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