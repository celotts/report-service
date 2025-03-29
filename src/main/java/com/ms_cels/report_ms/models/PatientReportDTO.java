package com.ms_cels.report_ms.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientReportDTO {
    private String reportId;
    private String issueDate;
    private String generatedReport;
    private String patientId;
    private String patientName;
}