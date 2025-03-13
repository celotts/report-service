package com.ms_cels.report_ms.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
public class LoadBalancerConfiguration {

    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(ConfigurableApplicationContext context) {
        log.info("Configurando LoadBalancer");
        return ServiceInstanceListSupplier.builder()
                .withBlockingDiscoveryClient()
                .withSameInstancePreference()
                .build(context);
    }
}
