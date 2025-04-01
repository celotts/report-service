package com.ms_cels.report_ms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientReport {
    private String reportId;
    private String issueDate;
    private String generatedReport;
    private String patientId;
    private String patientName;
    private String email;
}