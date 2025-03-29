package com.ms_cels.report_ms;

import com.netflix.discovery.EurekaClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ReportMsApplication implements CommandLineRunner {

	private final EurekaClient eurekaClient;

	public ReportMsApplication(EurekaClient eurekaClient) {
		this.eurekaClient = eurekaClient;
	}

	public static void main(String[] args) {
		SpringApplication.run(ReportMsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Mostrar todas las regiones conocidas
		this.eurekaClient.getAllKnownRegions().forEach(System.out::println);

		// Listar todas las aplicaciones registradas
		System.out.println("Applications registered in Eureka:");
		this.eurekaClient.getApplications().getRegisteredApplications().forEach(app -> {
			System.out.println("- " + app.getName() + " (" + app.getInstances().size() + " instancias)");
			app.getInstances().forEach(instance -> {
				System.out.println("  * " + instance.getHostName() + ":" + instance.getPort() + " - Status: " + instance.getStatus());
			});
		});

		// Buscar específicamente el servicio patient-service (puede ser null si no está registrado)

		System.out.println("\nLooking for PATIENT-SERVICE application:");
		com.netflix.discovery.shared.Application patientApp = this.eurekaClient.getApplication("PATIENT-SERVICE");
		if (patientApp != null) {
			System.out.println("PATIENT-SERVICE found with " + patientApp.getInstances().size() + " instancias");
		} else {
			System.out.println("PATIENT-SERVICE not found in the Eureka log");
		}
	}
}
