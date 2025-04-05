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
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String birthDate;
    private String address;
    private String gender;
    private String bloodType;
}