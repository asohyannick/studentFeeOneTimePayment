package com.leedTech.studentFeeOneTimePayment.config.swaggerConfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {

		@Value("${api.version}")
		private String apiVersion;
		
		private static final String SECURITY_SCHEME_NAME = "BearerAuth";
		
		@Bean
		public OpenAPI openAPI() {
			return new OpenAPI()
					       .info(apiInfo())
					       .addSecurityItem(securityRequirement())
					       .components(securityComponents());
		}
		
		private Info apiInfo() {
			return new Info()
					       .title("LeedTech Student Fee One-Time Payment API Developed By Asoh Yannick, Java Software Engineer.")
					       .description("""
		                        REST API for managing student fee one-time payments
		                        in the LeedTech School Management System.
		                        
		                        ## Features
		                        - User Authentication & Authorization
		                        - Student Fee Management
		                        - One-Time Payment Processing via Stripe
		                        - Role-Based Access Control
		                        """)
					       .version(apiVersion)
					       .contact(apiContact())
					       .license(apiLicense());
		}
		
		private Contact apiContact() {
			return new Contact()
					       .name("LeedTech Support")
					       .email("support@leedtech.com")
					       .url("https://www.leedtech.com");
		}
		
		private License apiLicense() {
			return new License()
					       .name("MIT License")
					       .url("https://opensource.org/licenses/MIT");
		}
		
		
		private SecurityRequirement securityRequirement() {
			return new SecurityRequirement()
					       .addList(SECURITY_SCHEME_NAME);
		}
		
		private Components securityComponents() {
			return new Components()
					       .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
							                                                 .name(SECURITY_SCHEME_NAME)
							                                                 .type(SecurityScheme.Type.HTTP)
							                                                 .scheme("bearer")
							                                                 .bearerFormat("JWT")
							                                                 .description("Paste your JWT access token here. Example: eyJhbGci...")
					       );
		}
}