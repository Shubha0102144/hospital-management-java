package com.hospital.management.doctor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctors")
public class DoctorDocument {

    @Id
    private int id;

    private String name;
    
    @Indexed(unique = true)
    private String licencenumber;
    
    private String specialty;
    private String department;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return name;
    }

    public void setFullName(String name) {
        this.name = name;
    }

    public String getLicenceNumber() {
        return licencenumber;
    }

    public void setLicenceNumber(String licencenumber) {
        this.licencenumber = licencenumber;
    }

    public String getspecialty() {
        return specialty;
    }

    public void setspecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getdepartment() {
        return department;
    }

    public void setdepartment(String department) {
        this.department = department;
    }

}