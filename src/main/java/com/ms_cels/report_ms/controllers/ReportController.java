package com.ms_cels.report_ms.controllers;

import com.ms_cels.report_ms.services.ReportService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;


    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Report Service is running");
    }

    @GetMapping("/report/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Report controller is working");
    }

    @GetMapping("/report/{id}")
    public ResponseEntity<Map<String, Object>> getReport(@PathVariable("id") String id) {
        // Validación inicial del ID
        if (!isValidUUID(id)) {
            log.warn("ID inválido: {}", id);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "ID de paciente inválido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        int maxRetries = 3;
        for (int retryCount = 0; retryCount < maxRetries; retryCount++) {
            try {
                return ResponseEntity.ok(this.reportService.makeReport(id));
            } catch (FeignException.BadRequest e) {
                // Manejo específico para errores 400
                log.error("Error de solicitud para ID {}: {}", id, e.getMessage());
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Solicitud inválida: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            } catch (FeignException.NotFound e) {
                // Manejo específico para recursos no encontrados
                log.error("Paciente no encontrado para ID {}: {}", id, e.getMessage());
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Paciente no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            } catch (feign.RetryableException e) {
                log.warn("Intento {} de {}: Error de conexión al servicio de pacientes: {}.",
                        retryCount + 1, maxRetries, e.getMessage());

                if (retryCount == maxRetries - 1) {
                    // Último intento, no hacer espera
                    continue;
                }

                try {
                    // Exponential backoff con un máximo de 2 segundos
                    long waitTime = Math.min(500L * (1L << retryCount), 2000L);
                    log.info("Esperando {}ms antes de reintentar...", waitTime);
                    TimeUnit.MILLISECONDS.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "Operación interrumpida");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
                }
            } catch (Exception e) {
                log.error("Error al generar el reporte: {}", e.getMessage(), e);
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Error inesperado al generar el reporte");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        }

        // Si llegamos aquí, se agotaron los reintentos
        Map<String, Object> error = new HashMap<>();
        error.put("error", "No se pudo conectar con el servicio de pacientes después de " + maxRetries + " intentos");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    /**
     * Valida si el ID proporcionado es un UUID válido.
     *
     * @param id Cadena a validar
     * @return true si es un UUID válido, false en caso contrario
     */
    private boolean isValidUUID(String id) {
        if (id == null) {
            return false;
        }

        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}