package com.ayush.blog;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Spring Boot blog Rest Api Documentation",
				description = "Blog Apis for sleep and mental health app",
				version = "v1.0",
				contact = @Contact(
						name = "Ayush Aryan",
						email = "ayusharyan1309@gmail.com",
						url = "https://ayusharyan13.github.io/portfolio/"
				),
				license = @License(
						name = "Ayush Aryan",
						url = "https://www.linkedin.com/in/ayusharyan1309/"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Github repo for reference",
				url = "https://github.com/ayusharyan13/Sleep-prediction-app"
		)
)
public class SpringBootBlogAppApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBlogAppApplication.class, args);
	}

}
