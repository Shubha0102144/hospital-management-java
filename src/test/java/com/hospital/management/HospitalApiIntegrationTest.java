package com.hospital.management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.doctor.model.DoctorDocument;
import com.hospital.management.doctor.repository.DoctorRepository;
import com.hospital.management.patient.model.Gender;
import com.hospital.management.patient.model.PatientDocument;
import com.hospital.management.patient.repository.PatientRepository;
import com.hospital.management.support.MongoTestcontainersConfig;
import com.hospital.management.user.model.Role;
import com.hospital.management.user.model.UserDocument;
import com.hospital.management.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class HospitalApiIntegrationTest extends MongoTestcontainersConfig {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private String patientId;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    patientRepository.deleteAll();

    UserDocument admin = new UserDocument();
    admin.setEmail("admin@hospital.local");
    admin.setPasswordHash(passwordEncoder.encode("ChangeMe123!"));
    admin.setFullName("Hospital Admin");
    admin.setRoles(Set.of(Role.ADMIN));
    admin.setEnabled(true);
    admin.setCreatedAt(Instant.now());
    admin.setUpdatedAt(Instant.now());
    userRepository.save(admin);

    UserDocument patientUser = new UserDocument();
    patientUser.setEmail("patient@hospital.local");
    patientUser.setPasswordHash(passwordEncoder.encode("PatientPass1"));
    patientUser.setFullName("Hospital Patient");
    patientUser.setRoles(Set.of(Role.PATIENT));
    patientUser.setEnabled(true);
    patientUser.setCreatedAt(Instant.now());
    patientUser.setUpdatedAt(Instant.now());
    userRepository.save(patientUser);

    PatientDocument patient = new PatientDocument();
    patient.setFullName("Doctor");
    patient.setPrimaryDoctorName("Dr. Seed");
    patient.setMedicalRecordNumber("MRN-100001");
    patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
    patient.setGender(Gender.UNKNOWN);
    patient.setNotes("A seeded patient");
    patient.setCreatedAt(Instant.now());
    patient.setUpdatedAt(Instant.now());
    patientId = patientRepository.save(patient).getId();

    DoctorDocument doctor = new DoctorDocument();
    doctor.setfullName("Seed Patient");
    doctor.setLicencenumber("abc");
    doctor.setSpecialty("doc");
    doctor.setDepartment("hdbdb");
    doctorRepository.save(doctor);

  }

  @Test
  void registerLoginAndUpdatePatientHappyPath() throws Exception {
    String email = "intern+" + UUID.randomUUID() + "@hospital.local";

    mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "email": "%s",
              "password": "InternPass1",
              "fullName": "New Intern"
            }
            """.formatted(email)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value(email))
        .andExpect(jsonPath("$.roles[0]").value("PATIENT"));

    String adminToken = login("admin@hospital.local", "ChangeMe123!");

    mockMvc.perform(put("/api/v1/patients/{id}", patientId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(validUpdatePayload()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullName").value("Ada Lovelace"))
        .andExpect(jsonPath("$.medicalRecordNumber").value("MRN-100002"))
        .andExpect(jsonPath("$.dateOfBirth").value("1815-12-10"));

    assertThat(patientRepository.findById(patientId)).isPresent();
    assertThat(patientRepository.findById(patientId).orElseThrow().getFullName()).isEqualTo("Ada Lovelace");
  }

  @Test
  void rejectInvalidAuthenticationInformation() throws Exception {
    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "email": "admin1@hospital.local",
              "password": "tdsetgg123"
            }
            """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void registerRejectsInvalidPayload() throws Exception {
    mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "email": "not-an-email",
              "password": "short",
              "fullName": ""
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"))
        .andExpect(jsonPath("$.violations").isArray());
  }

  @Test
  void registerRejectsInvalidDoctorField() throws Exception {
    mockMvc.perform(post("/api/v1/doctors")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "fullName": " ",
              "licenceNumber": " ",
              "speciality": " ",
              "department": " "
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"))
        .andExpect(jsonPath("$.violations").isArray());
  }

  @Test
  void updatePatientRequiresAuthentication() throws Exception {
    mockMvc.perform(put("/api/v1/patients/{id}", patientId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(validUpdatePayload()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void updatePatientForbidsPatientRole() throws Exception {
    String patientToken = login("patient@hospital.local", "PatientPass1");

    mockMvc.perform(put("/api/v1/patients/{id}", patientId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + patientToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(validUpdatePayload()))
        .andExpect(status().isForbidden());
  }

  @Test
  void loginRejectsBadPassword() throws Exception {
    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "email": "admin@hospital.local",
              "password": "WrongPassword1"
            }
            """))
        .andExpect(status().isUnauthorized());
  }

  private String login(String email, String password) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "email": "%s",
              "password": "%s"
            }
            """.formatted(email, password)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andReturn();

    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    return body.get("accessToken").asText();
  }

  private String validUpdatePayload() {
    return """
        {
          "fullName": "Ada Lovelace",
          "primaryDoctorName": "Dr. Grace Hopper",
          "medicalRecordNumber": "MRN-100002",
          "dateOfBirth": "1815-12-10",
          "gender": "FEMALE",
          "phone": "+15551230001",
          "bloodGroup": "O+",
          "notes": "Updated by integration test"
        }
        """;
  }
}
