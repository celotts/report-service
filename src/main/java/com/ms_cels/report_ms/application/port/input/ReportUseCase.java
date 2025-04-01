package com.ms_cels.report_ms.application.port.input;

import com.ms_cels.report_ms.domain.model.PatientReport;
import java.util.Map;

public interface ReportUseCase {
    Map<String, Object> makeReport(String id);
    PatientReport generatePatientReport(String id);
    String saveReport(String idReport);
    void deleteReport(String name);
}