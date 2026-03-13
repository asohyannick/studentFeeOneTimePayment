package com.leedTech.studentFeeOneTimePayment.config.corsConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

@Value("${cors.allowed-origins}")
private String allowedOrigins;

@Value("${cors.allowed-methods}")
private String allowedMethods;

@Bean
public CorsConfigurationSource corsConfigurationSource() {
	CorsConfiguration configuration = new CorsConfiguration();
	
	List<String> origins = Arrays.stream(allowedOrigins.split(","))
			                       .map(String::trim)
			                       .toList();
	configuration.setAllowedOrigins(origins);
	
	List<String> methods = Arrays.stream(allowedMethods.split(","))
			                       .map(String::trim)
			                       .toList();
	configuration.setAllowedMethods(methods);
	
	configuration.setAllowedHeaders(List.of(
			"Authorization",
			"Content-Type",
			"Accept",
			"X-Requested-With",
			"Origin",
			"Access-Control-Request-Method",
			"Access-Control-Request-Headers"
	));
	
	configuration.setExposedHeaders(List.of(
			"Authorization",
			"Access-Control-Allow-Origin",
			"Access-Control-Allow-Credentials"
	));
	
	configuration.setAllowCredentials(true);
	configuration.setMaxAge(3600L);
	
	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	source.registerCorsConfiguration("/**", configuration);
	
	return source;
}
}