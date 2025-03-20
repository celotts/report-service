package com.ms_cels.report_ms.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Patient {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private String bloodType;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactName2;
    private String emergencyContactName3;
    private String emergencyContactPhone2;
    private String emergencyContactPhone3;
    private String medicalHistory;
    private String allergies;
    private String insuranceProvider;
    private LocalDateTime registrationDate;
    private LocalDateTime updatedAt;
    private Boolean status;
    private Boolean active;
    private LocalDate dateBirth;
}