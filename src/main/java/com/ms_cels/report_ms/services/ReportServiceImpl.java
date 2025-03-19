package com.ms_cels.report_ms.services;

import com.ms_cels.report_ms.models.Patient;
import com.ms_cels.report_ms.repositories.PatientRepository;
import com.netflix.discovery.EurekaClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class ReportServiceImpl extends ReportService {

    private final PatientRepository patientRepository;
    private final EurekaClient eurekaClient;

    // Caché local para almacenar pacientes por ID
    private static final Map<String, Patient> patientCache = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> makeReport(String id) {
        // Primero intentar obtener el paciente de la caché
        log.info("Verificando caché para paciente con ID {}", id);
        Patient patient = patientCache.get(id);
        log.info("Estado de caché para ID {}: {}", id, patient == null ? "NO ENCONTRADO" : "ENCONTRADO");

        if (patient == null) {
            log.info("Paciente con ID {} no encontrado en caché, consultando servicio", id);
            Optional<Patient> patientOptional = patientRepository.getById(id);

            if (patientOptional.isPresent()) {
                patient = patientOptional.get();
                // Almacenar en caché para futuras solicitudes
                patientCache.put(id, patient);
                log.info("Paciente con ID {} almacenado en caché", id);
            } else {
                log.warn("No se encontró paciente con ID: {}", id);
                Map<String, Object> errorReport = new HashMap<>();
                errorReport.put("error", "No se encontró paciente con ID: " + id);
                return errorReport;
            }
        } else {
            log.info("Paciente con ID {} obtenido desde caché local", id);
        }

        // Generar el reporte usando el paciente (ya sea de caché o recién obtenido)
        Map<String, Object> report = new HashMap<>();
        report.put("id", patient.getId());
        report.put("nombre", patient.getFirstName() + " " + patient.getLastName());
        report.put("genero", patient.getGender() != null ? patient.getGender() : "No especificado");
        report.put("email", patient.getEmail() != null ? patient.getEmail() : "No especificado");

        log.info("Reporte básico generado para el paciente con ID: {}", id);
        return report;
    }

    @Override
    String saveReport(String idReport) {
        log.info("Guardando reporte con ID: {}", idReport);
        return "Reporte guardado con id: " + idReport;
    }

    @Override
    void deleteReport(String name) {
        log.info("Eliminando reporte: {}", name);
        System.out.println("Reporte eliminado: " + name);
    }

    // Método opcional para limpiar la caché o refrescar un paciente específico
    public void refreshPatientCache(String id) {
        patientCache.remove(id);
        log.info("Paciente con ID {} eliminado de la caché", id);
    }
}