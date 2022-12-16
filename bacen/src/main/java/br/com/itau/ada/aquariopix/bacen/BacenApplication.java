package br.com.itau.ada.aquariopix.bacen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BacenApplication {

	public static void main(String[] args) {
		SpringApplication.run(BacenApplication.class, args);
	}

}
