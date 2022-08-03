package fr.backendt.cvebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CveBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CveBotApplication.class, args);
	}

}
