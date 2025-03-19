package com.ms_cels.report_ms;

import com.ms_cels.report_ms.models.Patient;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ReportMsApplication implements CommandLineRunner {

	@Autowired
	private EurekaClient eurekaClient;

	public static void main(String[] args) {
		SpringApplication.run(ReportMsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Mostrar todas las regiones conocidas
		this.eurekaClient.getAllKnownRegions().forEach(System.out::println);

		// Listar todas las aplicaciones registradas
		System.out.println("Aplicaciones registradas en Eureka:");
		this.eurekaClient.getApplications().getRegisteredApplications().forEach(app -> {
			System.out.println("- " + app.getName() + " (" + app.getInstances().size() + " instancias)");
			app.getInstances().forEach(instance -> {
				System.out.println("  * " + instance.getHostName() + ":" + instance.getPort() + " - Status: " + instance.getStatus());
			});
		});

		// Buscar específicamente el servicio patient-service (puede ser null si no está registrado)
		System.out.println("\nBuscando aplicación PATIENT-SERVICE:");
		com.netflix.discovery.shared.Application patientApp = this.eurekaClient.getApplication("PATIENT-SERVICE");
		if (patientApp != null) {
			System.out.println("PATIENT-SERVICE encontrado con " + patientApp.getInstances().size() + " instancias");
		} else {
			System.out.println("PATIENT-SERVICE no encontrado en el registro de Eureka");
		}
	}
}
