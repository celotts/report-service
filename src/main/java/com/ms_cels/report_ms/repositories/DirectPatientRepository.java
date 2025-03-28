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
            log.info("Consultando directamente al servicio en: {}", PATIENT_SERVICE_URL + id);
            Patient patient = restTemplate.getForObject(PATIENT_SERVICE_URL + id, Patient.class);
            log.info("Respuesta directa: {}", patient != null ? "Encontrado" : "No encontrado");
            return Optional.ofNullable(patient);
        } catch (Exception e) {
            log.error("Error al obtener paciente directamente: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}