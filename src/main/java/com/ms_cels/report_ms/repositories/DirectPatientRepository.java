package com.ms_cels.report_ms.repositories;

import com.ms_cels.report_ms.models.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@Slf4j
public class DirectPatientRepository {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PATIENT_SERVICE_URL = "http://localhost:8082/api/v1/patients/";

    public Optional<Patient> getPatientById(String id) {
        try {
            log.info("By consulting the service directly in: {}", PATIENT_SERVICE_URL + id);
            Patient patient = restTemplate.getForObject(PATIENT_SERVICE_URL + id, Patient.class);
            log.info("Direct response: {}", patient != null ? "Found" : "Not found");
            return Optional.ofNullable(patient);
        } catch (Exception e) {
            log.error("Error getting patient directly: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}