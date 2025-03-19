package com.ms_cels.report_ms.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Patient {
    private UUID id;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
}