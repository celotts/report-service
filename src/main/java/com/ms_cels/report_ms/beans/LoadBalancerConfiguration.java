package com.ms_cels.report_ms.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@Slf4j
public class LoadBalancerConfiguration {

    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(ConfigurableApplicationContext context) {
        log.info("Configuring load balancer");
        return ServiceInstanceListSupplier
                .builder()
                .withBlockingDiscoveryClient()
                .withZonePreference()  // Nota se puede usar otra estrategia como withCaching(), withHealthChecks(), etc.
                .build(context);
    }
}