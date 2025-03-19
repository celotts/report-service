package com.ms_cels.report_ms.services;

import com.ms_cels.report_ms.models.Patient;
import com.ms_cels.report_ms.repositories.PatientRepository;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
@Slf4j
public class ReportServiceImpl extends ReportService {
    private final PatientRepository patientRepository;
    private final EurekaClient eurekaClient;

    // Caché local para almacenar pacientes por ID
    private static final Map<String, Patient> patientCache = new ConcurrentHashMap<>();

    @Override
    @CircuitBreaker(name = "patientServiceBreaker", fallbackMethod = "fallbackMakeReport")
    public Map<String, Object> makeReport(String id) {
        // Primero intentar obtener el paciente de la caché
        log.info("Verificando caché para paciente con ID {}", id);
        Patient patient = patientCache.get(id);
        log.info("Estado de caché para ID {}: {}", id, patient == null ? "NO ENCONTRADO" : "ENCONTRADO");

        if (patient == null) {
            log.info("Paciente con ID {} no encontrado en caché, consultando servicio", id);
            patient = fetchPatientWithTimeout(id);

            if (patient != null) {
                // Almacenar en caché para futuras solicitudes
                patientCache.put(id, patient);
                log.info("Paciente con ID {} almacenado en caché", id);
            } else {
                log.warn("No se encontró paciente con ID: {}", id);
                return createErrorReport(id);
            }
        } else {
            log.info("Paciente con ID {} obtenido desde caché local", id);
        }

        // Generar el reporte usando el paciente (ya sea de caché o recién obtenido)
        return createReportFromPatient(patient);
    }

    // Método para obtener paciente con timeout controlado
    private Patient fetchPatientWithTimeout(String id) {
        CompletableFuture<Optional<Patient>> future = CompletableFuture.supplyAsync(() ->
                patientRepository.getById(id)
        );

        try {
            return future.get(3, TimeUnit.SECONDS) // Timeout de 3 segundos
                    .orElse(null);
        } catch (TimeoutException e) {
            log.warn("Timeout al obtener paciente con ID: {}", id);
            return null;
        } catch (Exception e) {
            log.error("Error al obtener paciente", e);
            return null;
        }
    }

    // Método de fallback para manejar errores
    public Map<String, Object> fallbackMakeReport(String id, Throwable throwable) {
        log.error("Fallback para paciente ID: {}", id, throwable);
        return createErrorReport(id);
    }

    // Método para crear reporte de error
    private Map<String, Object> createErrorReport(String id) {
        Map<String, Object> errorReport = new HashMap<>();
        errorReport.put("error", "No se encontró paciente con ID: " + id);
        return errorReport;
    }

    // Método para crear reporte desde paciente
    private Map<String, Object> createReportFromPatient(Patient patient) {
        Map<String, Object> report = new HashMap<>();
        report.put("id", patient.getId());
        report.put("nombre", patient.getFirstName() + " " + patient.getLastName());
        report.put("genero", patient.getGender() != null ? patient.getGender() : "No especificado");
        report.put("email", patient.getEmail() != null ? patient.getEmail() : "No especificado");

        log.info("Reporte básico generado para el paciente con ID: {}", patient.getId());
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