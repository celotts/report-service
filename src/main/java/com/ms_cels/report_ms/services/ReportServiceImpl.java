package com.ms_cels.report_ms.services;

import com.ms_cels.report_ms.helpers.ReportHelper;
import com.ms_cels.report_ms.models.Patient;
import com.ms_cels.report_ms.repositories.DirectPatientRepository;
import com.ms_cels.report_ms.repositories.PatientRepository;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.Getter;
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
    @Getter
    private final PatientRepository patientRepository;
    private final ReportHelper reportHelper;
    @Getter
    private final EurekaClient eurekaClient;

    @Autowired
    private DirectPatientRepository directPatientRepository;

    // Local cache to store patients by ID
    private static final Map<String, Patient> patientCache = new ConcurrentHashMap<>();

    // Constructor with required fields
    @Autowired
    public ReportServiceImpl(PatientRepository patientRepository, ReportHelper reportHelper, EurekaClient eurekaClient) {
        this.patientRepository = patientRepository;
        this.reportHelper = reportHelper;
        this.eurekaClient = eurekaClient;
    }

    @Override
    @CircuitBreaker(name = "patientServiceBreaker", fallbackMethod = "fallbackMakeReport")
    public Map<String, Object> makeReport(String id) {
        // First try to get the patient from cache

        reportHelper.readTemplate();

        log.info("Checking cache for patient with ID {}", id);
        Patient patient = patientCache.get(id);

        log.info("Cache status for ID {}: {}", id, patient == null ? "NOT FOUND" : "FOUND");

        if (patient == null) {
            log.info("Patient with ID {} not found in cache, querying service", id);
            patient = fetchPatientWithTimeout(id);

            if (patient != null) {
                // Store in cache for future requests
                patientCache.put(id, patient);
                log.info("Patient with cached ID {}", id);
            } else {
                log.warn("No patient found with ID: {}", id);
                return createErrorReport(id);
            }
        } else {
            log.info("Patient with ID {} obtained from local cache", id);
        }

        // Generate the report using the patient (either from cache or newly obtained)
        return createReportFromPatient(patient);
    }

    // Method to get patient with controlled timeout
    private Patient fetchPatientWithTimeout(String id) {
        log.info("Trying to get patient with ID {} directly", id);

        CompletableFuture<Optional<Patient>> future = CompletableFuture.supplyAsync(() ->
                directPatientRepository.getPatientById(id)
        );

        try {
            Optional<Patient> result = future.get(10, TimeUnit.SECONDS); // Increased to 10 seconds
            log.info("Direct query result for ID {}: {}",
                    id, result.isPresent() ? "Found" : "Not found");
            return result.orElse(null);
        } catch (TimeoutException e) {
            log.warn("Timeout when getting patient with ID: {}", id);
            return null;
        } catch (Exception e) {
            log.error("Error getting patient ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    // Fallback method to handle errors
    public Map<String, Object> fallbackMakeReport(String id, Throwable throwable) {
        log.error("Fallback for patient ID: {}", id, throwable);
        return createErrorReport(id);
    }

    // Method to create error report
    private Map<String, Object> createErrorReport(String id) {
        Map<String, Object> errorReport = new HashMap<>();
        errorReport.put("error", "No patient with ID was found: " + id);
        return errorReport;
    }

    // Method to create report from patient
    private Map<String, Object> createReportFromPatient(Patient patient) {
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("id", patient.getId());
        reportData.put("name", patient.getFirstName() != null ? patient.getFirstName() : "Name Not specified");
        reportData.put("lastName", patient.getLastName() != null ? patient.getLastName() : "LastName Not specified");
        reportData.put("birthDate", patient.getBirthDate() != null ? patient.getBirthDate() : "BirthDate Not specified");

        // Calculate age from birth date
        if (patient.getBirthDate() != null) {
            try {
                // Convert String to LocalDate using correct format
                String dateStr = patient.getBirthDate().toString();
                // If date includes time, take only the date part
                if (dateStr.contains("T")) {
                    dateStr = dateStr.split("T")[0];
                }
                java.time.LocalDate birthDate = java.time.LocalDate.parse(dateStr);
                int age = java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
                reportData.put("age", String.valueOf(age));
            } catch (Exception e) {
                log.error("The age could not be calculated for the date: {}", patient.getBirthDate(), e);
                reportData.put("age", "Not specified");
            }
        } else {
            reportData.put("age", "Not specified");
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

        // Change to English names to match PatientReportDTO
        reportData.put("issueDate", java.time.LocalDateTime.now().toString());
        reportData.put("reportId", java.util.UUID.randomUUID().toString());

        // Get the template and process it
        String template = reportHelper.readTemplate();
        String processedReport = reportHelper.processTemplate(template, reportData);

        // Change the field name to generatedReport in English
        Map<String, Object> result = new HashMap<>(reportData);
        result.put("generatedReport", processedReport);

        log.info("Basic report generated for the patient with ID: {}", patient.getId());
        return result;
    }

    @Override
    String saveReport(String idReport) {
        log.info("Saving report with ID: {}", idReport);
        return "Report saved with id: " + idReport;
    }

    @Override
    void deleteReport(String name) {
        log.info("Deleting report: {}", name);
        System.out.println("Report deleted: " + name);
    }

    // Optional method to clear cache or refresh a specific patient
    public void refreshPatientCache(String id) {
        patientCache.remove(id);
        log.info("Patient with ID {} removed from cache", id);
    }
}