package com.ms_cels.report_ms.repositories;

import com.ms_cels.report_ms.models.Patient;
import com.ms_cels.report_ms.models.PatientReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@Slf4j
public class DirectPatientRepository {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PATIENT_SERVICE_URL = "http://localhost:8082/api/v1/patients/";

    public Optional<Patient> getPatientById(String id) {
        try {
            var dto = restTemplate.getForObject(PATIENT_SERVICE_URL + id, PatientReportDTO.class);
            assert dto != null;
            return Optional.of(toDomain(dto));
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Paciente con ID {} no encontrado", id);
            return Optional.empty();
        } catch (ResourceAccessException e) {
            log.error("No se pudo conectar al servicio de pacientes: {}", e.getMessage());
            return Optional.empty(); // ⚠️ Aquí devolvemos vacío en vez de lanzar excepción
        }
    }

    private Patient toDomain(PatientReportDTO dto) {
        return new Patient(
                dto.getId(),
                dto.getName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getBirthDate(),
                dto.getAddress(),
                dto.getGender(),
                dto.getBloodType()
        );
    }
}