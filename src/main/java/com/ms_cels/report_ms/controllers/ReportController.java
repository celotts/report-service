package com.ms_cels.report_ms.controllers;

import com.ms_cels.report_ms.services.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
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
        Map<String, Object> fullReport = reportService.makeReport(id);

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


}