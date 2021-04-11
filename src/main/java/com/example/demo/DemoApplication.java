package com.example.demo;

import com.example.demo.config.example.BatchConfiguration;
import com.example.demo.config.jpa.BatchJPAJobConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({BatchConfiguration.class, BatchJPAJobConfiguration.class})
public class DemoApplication {

	public static void main(String[] args) {
		System.exit(
				SpringApplication.exit(
						SpringApplication.run(
								DemoApplication.class,
								args
						)
				)
		);
	}
}
