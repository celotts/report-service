package com.ms_cels.report_ms.services;

import com.ms_cels.report_ms.repositories.PatientRepository;
import com.netflix.discovery.EurekaClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ReportServiceImpl extends ReportService {

    private PatientRepository patientRepository;

    @Override
    String makeReport(String id) {
        return "Reporte generado con id: " + id;
    }

    @Override
    String saveReport(String idReport) {
        return "Reporte guardado con id: " + idReport;
    }

    @Override
    void deleteReport(String name) {
        System.out.println("Reporte eliminado: " + name);
    }

}
