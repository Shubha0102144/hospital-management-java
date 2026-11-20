package com.hospital.management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        private static final String BEARER_SCHEME = "bearerAuth";

        @Bean
        public OpenAPI hospitalOpenApi() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Hospital Management API")
                                                .description("""
                                                                Half-baked hospital management backend for intern onboarding.

                                                                Fully implemented reference slices:
                                                                - POST /api/v1/users — public patient registration
                                                                - POST /api/v1/auth/login — JWT login
                                                                - PUT /api/v1/patients/{id} — secured patient update (ADMIN, DOCTOR)
                                                                - POST /api/v1/doctors -public doctor registartion(ADMIN,DOCTOR)
                                                                - GET  /api/v1/doctors/{id} - view a doctor(ADMIN,DOCTOR,PATIENT)
                                                                - GET  /api/v1/doctors/search?name -search doctors(ADMIN,DOCTOR,PATIENT)
                                                                - PUT /api/v1/doctors/{id} -secured doctor update(ADMIN,DOCTOR)
                                                                The Doctor resource is intentionally left unimplemented for interns.
                                                                """)
                                                .version("0.1.0")
                                                .contact(new Contact().name("Hospital Engineering")
                                                                .email("engineering@hospital.local")))
                                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                                                new SecurityScheme()
                                                                .name(BEARER_SCHEME)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")));
        }
}
