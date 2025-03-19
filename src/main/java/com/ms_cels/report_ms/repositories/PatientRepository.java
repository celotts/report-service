package com.ms_cels.report_ms.repositories;

import com.ms_cels.report_ms.beans.LoadBalancerConfiguration;
import com.ms_cels.report_ms.models.Patient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "patient-service")
@LoadBalancerClient(name = "patient-service", configuration = LoadBalancerConfiguration.class)
public interface PatientRepository {
    @GetMapping("/api/v1/patients/{id}")
    Optional<Patient> getById(@PathVariable String id);
}