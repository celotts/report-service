package com.ms_cels.report_ms.controllers;

import com.ms_cels.report_ms.application.port.input.ReportUseCase;
import com.ms_cels.report_ms.domain.model.PatientReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class ReportController {

    private final ReportUseCase reportUseCase;

    @Autowired
    public ReportController(@Qualifier("hexagonalReportService") ReportUseCase reportUseCase) {
        this.reportUseCase = reportUseCase;
    }


    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Report Service is running");
    }

    @GetMapping("/report/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Report controller is working");
    }

    @GetMapping("/report/{id}")
    public ResponseEntity<Map<String, Object>> generatePatientReport(@PathVariable String id) {
        Map<String, Object> fullReport = reportUseCase.makeReport(id);

        if (fullReport.containsKey("error")) {
            return ResponseEntity.notFound().build();
        }

        // Crear un mapa simplificado solo con la información relevante
        Map<String, Object> simplifiedReport = new HashMap<>();
        simplifiedReport.put("reportId", fullReport.get("reportId"));
        simplifiedReport.put("issueDate", fullReport.get("issueDate"));
        simplifiedReport.put("generatedReport", fullReport.get("generatedReport"));
        simplifiedReport.put("patientId", fullReport.get("id"));
        simplifiedReport.put("patientName", fullReport.get("name") + " " + fullReport.get("lastName"));
        simplifiedReport.put("email", fullReport.get("email"));

        return ResponseEntity.ok(simplifiedReport);
    }

    @GetMapping("/report/generate/{id}")
    public ResponseEntity<PatientReport> generatePatientReportDetails(@PathVariable String id) {
        try {
            return ResponseEntity.ok(reportUseCase.generatePatientReport(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/report/save")
    public ResponseEntity<String> saveReport(@RequestParam String idReport) {
        String result = reportUseCase.saveReport(idReport);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/report/{name}")
    public ResponseEntity<Void> deleteReport(@PathVariable String name) {
        reportUseCase.deleteReport(name);
        return ResponseEntity.noContent().build();
    }





}