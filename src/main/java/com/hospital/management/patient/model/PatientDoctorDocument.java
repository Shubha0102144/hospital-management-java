package com.hospital.management.patient.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "patients-doctors")
@CompoundIndexes({
        @CompoundIndex(name = "patient_doctor_index", def = "{'doctorId':1,'patientId':1}", unique = true)
})
public class PatientDoctorDocument {

    private String doctorId;
    private String patientId;

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

}
