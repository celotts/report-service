package com.ms_cels.report_ms.services;

import com.ms_cels.report_ms.helpers.ReportHelper;
import com.ms_cels.report_ms.models.Patient;
import com.ms_cels.report_ms.repositories.DirectPatientRepository;
import com.ms_cels.report_ms.repositories.PatientRepository;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class ReportServiceImpl extends ReportService {
    private final PatientRepository patientRepository;
    private final ReportHelper reportHelper;
    private final EurekaClient eurekaClient;

    @Autowired
    private DirectPatientRepository directPatientRepository;

    // Caché local para almacenar pacientes por ID
    private static final Map<String, Patient> patientCache = new ConcurrentHashMap<>();

    // Constructor con los campos requeridos
    @Autowired
    public ReportServiceImpl(PatientRepository patientRepository, ReportHelper reportHelper, EurekaClient eurekaClient) {
        this.patientRepository = patientRepository;
        this.reportHelper = reportHelper;
        this.eurekaClient = eurekaClient;
    }

    @Override
    @CircuitBreaker(name = "patientServiceBreaker", fallbackMethod = "fallbackMakeReport")
    public Map<String, Object> makeReport(String id) {
        // Primero intentar obtener el paciente de la caché

        reportHelper.readTemplate();

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
        log.info("Intentando obtener paciente con ID {} directamente", id);

        CompletableFuture<Optional<Patient>> future = CompletableFuture.supplyAsync(() ->
                directPatientRepository.getPatientById(id)
        );

        try {
            Optional<Patient> result = future.get(10, TimeUnit.SECONDS); // Aumentado a 10 segundos
            log.info("Resultado de consulta directa para ID {}: {}",
                    id, result.isPresent() ? "Encontrado" : "No encontrado");
            return result.orElse(null);
        } catch (TimeoutException e) {
            log.warn("Timeout al obtener paciente con ID: {}", id);
            return null;
        } catch (Exception e) {
            log.error("Error al obtener paciente con ID {}: {}", id, e.getMessage(), e);
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
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("id", patient.getId());
        reportData.put("name", patient.getFirstName() != null ? patient.getFirstName() : "Name Not specified");
        reportData.put("lastName", patient.getLastName() != null ? patient.getLastName() : "LastName Not specified");
        reportData.put("birthDate", patient.getBirthDate() != null ? patient.getBirthDate() : "BithDate Not specified");

        // Calcular la edad a partir de la fecha de nacimiento
        if (patient.getBirthDate() != null) {
            try {
                // Convertir String a LocalDate usando el formato correcto
                String dateStr = patient.getBirthDate().toString();
                // Si la fecha incluye tiempo, tomamos solo la parte de la fecha
                if (dateStr.contains("T")) {
                    dateStr = dateStr.split("T")[0];
                }
                java.time.LocalDate birthDate = java.time.LocalDate.parse(dateStr);
                int age = java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
                reportData.put("age", String.valueOf(age));
            } catch (Exception e) {
                log.error("No se pudo calcular la edad para la fecha: {}", patient.getBirthDate(), e);
                reportData.put("age", "No especificado");
            }
        } else {
            reportData.put("age", "No especificado");
        }

        reportData.put("gender", patient.getGender() != null ? patient.getGender() : "Gender Not specified");
        reportData.put("bloodType", patient.getBloodType() != null ? patient.getBloodType() : "BloodType Not specified");
        reportData.put("phone", patient.getPhone() != null ? patient.getPhone() : "Phone Not specified");
        reportData.put("email", patient.getEmail() != null ? patient.getEmail() : "EmailNot specified");
        reportData.put("address", patient.getAddress() != null ? patient.getAddress() : "Address Not specified");
        reportData.put("city", patient.getCity() != null ? patient.getCity() : "City Not specified");
        reportData.put("country", patient.getCountry() != null ? patient.getCountry() : "Country Not specified");
        reportData.put("postalCode", patient.getPostalCode() != null ? patient.getPostalCode() : "PostalCodeNot specified");
        reportData.put("emergencyContactName", patient.getEmergencyContactName() != null ? patient.getEmergencyContactName() : "EmergencyContactName Not specified");
        reportData.put("emergencyContactPhone", patient.getEmergencyContactPhone() != null ? patient.getEmergencyContactPhone() : "EmergencyContactPhone Not specified");
        reportData.put("emergencyContactName2", patient.getEmergencyContactName2() != null ? patient.getEmergencyContactName2() : "EmergencyContactName 2 Not specified");
        reportData.put("emergencyContactPhone2", patient.getEmergencyContactPhone2() != null ? patient.getEmergencyContactPhone2() : "emergencyContactPhone 2 Not specified");
        reportData.put("emergencyContactName3", patient.getEmergencyContactName3() != null ? patient.getEmergencyContactName3() : "EmergencyContactName 3 Not specified");
        reportData.put("emergencyContactPhone3", patient.getEmergencyContactPhone3() != null ? patient.getEmergencyContactPhone3() : "EmergencyContactPhone 3 Not specified");
        reportData.put("medicalHistory", patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "MedicalHistory Not specified");
        reportData.put("allergies", patient.getAllergies() != null ? patient.getAllergies() : "AllergiesNot specifieds");
        reportData.put("insuranceProvider", patient.getInsuranceProvider() != null ? patient.getInsuranceProvider() : "InsuranceProvider Not specified");
        reportData.put("insuranceNumber", patient.getRegistrationDate() != null ? patient.getRegistrationDate() : "InsuranceNumber Not specified");
        reportData.put("updatedAt", patient.getUpdatedAt() != null ? patient.getUpdatedAt() : "UpdatedAt Not specified");
        reportData.put("status", patient.getStatus() != null ? patient.getStatus() : "Status Not specified");
        reportData.put("active", patient.getActive() != null ? patient.getActive() : "Not specified");

        // Añadir la fecha de emisión del reporte y un ID único
        reportData.put("fechaEmision", java.time.LocalDateTime.now().toString());
        reportData.put("reporteId", java.util.UUID.randomUUID().toString());

        // Obtener la plantilla y procesarla
        String template = reportHelper.readTemplate();
        String processedReport = reportHelper.processTemplate(template, reportData);

        // Agregar el reporte procesado al mapa de resultados
        Map<String, Object> result = new HashMap<>(reportData);
        result.put("reporteGenerado", processedReport);

        log.info("Reporte básico generado para el paciente con ID: {}", patient.getId());
        return result;
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