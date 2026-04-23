package com.epam.gym.workload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan
public class WorkloadApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkloadApplication.class, args);
	}

}
