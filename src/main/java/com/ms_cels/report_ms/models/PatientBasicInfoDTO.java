package com.ms_cels.report_ms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientBasicInfoDTO {
    private UUID id;
    private String firstName;
    private String lastName;

    // Si necesitas el nombre completo
    public String getFullName() {
        return firstName + " " + lastName;
    }
}