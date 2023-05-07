package pe.maxz.keycloaksample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Product API with KeyCloak", version = "1.0.0"))

public class KeycloaksampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloaksampleApplication.class, args);
	}

}
