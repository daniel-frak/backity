package dev.codesoapbox.backity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BackityApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackityApplication.class, args);
	}
}
