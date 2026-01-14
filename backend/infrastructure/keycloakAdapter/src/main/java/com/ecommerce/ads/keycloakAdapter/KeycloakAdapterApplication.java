package com.ecommerce.ads.keycloakAdapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class KeycloakAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakAdapterApplication.class, args);
	}

}
