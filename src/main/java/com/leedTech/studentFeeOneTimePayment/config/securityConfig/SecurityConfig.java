package com.leedTech.studentFeeOneTimePayment.config.securityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leedTech.studentFeeOneTimePayment.config.corsConfig.CorsConfig;
import com.leedTech.studentFeeOneTimePayment.config.firebaseConfig.FirebaseAuthenticationFilter;
import com.leedTech.studentFeeOneTimePayment.config.jwtConfig.JWTAuthenticationFilter;
import com.leedTech.studentFeeOneTimePayment.exception.GlobalExceptionResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
				private final CorsConfig corsConfig;
				private final ObjectMapper objectMapper;
				private final JWTAuthenticationFilter jwtAuthenticationFilter;
				private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;
				@Value ( "${api.version}" )
				private String apiVersion;
				
				private String[] publicEndpoints ( ) {
					String base = "/api/" + apiVersion + "/auth";
					return new String[] {
							base + "/register" ,
							base + "/verify-otp" ,
							base + "/resend-otp" ,
							base + "/login" ,
							base + "/logout" ,
							base + "/google-login" ,
							base + "/magic-link/send" ,
							base + "/magic-link/verify" ,
							base + "/magic-link/resend" ,
							base + "/forgot-password" ,
							base + "/verify-reset-otp" ,
							base + "/reset-password" ,
							"/v3/api-docs/**" ,
							"/swagger-ui/**" ,
							"/swagger-ui.html",
							"/error"
					};
				}
				
				@Bean
				public BCryptPasswordEncoder passwordEncoder ( ) {
					return new BCryptPasswordEncoder ( );
				}
				
				@Bean
				public AuthenticationManager authenticationManager (
						AuthenticationConfiguration authenticationConfiguration
				) throws Exception {
					return authenticationConfiguration.getAuthenticationManager ( );
				}
				
				@Bean
				public AccessDeniedHandler accessDeniedHandler ( ) {
					return ( request , response , accessDeniedException ) -> {
						log.warn ( "Access denied for [{}] {}: {}" ,
								request.getMethod ( ) ,
								request.getRequestURI ( ) ,
								accessDeniedException.getMessage ( )
						);
						
						response.setStatus ( HttpStatus.FORBIDDEN.value ( ) );
						response.setContentType ( MediaType.APPLICATION_JSON_VALUE );
						response.setCharacterEncoding ( StandardCharsets.UTF_8.name ( ) );
						
						GlobalExceptionResponseHandler error = new GlobalExceptionResponseHandler (
								Instant.now ( ) ,
								"Access denied" ,
								"You do not have permission to access this resource" ,
								HttpStatus.FORBIDDEN.value ( ) ,
								"FORBIDDEN" ,
								request.getRequestURI ( ) ,
								request.getMethod ( )
						);
						
						response.getWriter ( ).write ( objectMapper.writeValueAsString ( error ) );
					};
				}
				
				@Bean
				public AuthenticationEntryPoint authenticationEntryPoint ( ) {
					return ( request , response , authException ) -> {
						log.warn ( "Unauthorized access for [{}] {}: {}" ,
								request.getMethod ( ) ,
								request.getRequestURI ( ) ,
								authException.getMessage ( )
						);
						
						response.setStatus ( HttpStatus.UNAUTHORIZED.value ( ) );
						response.setContentType ( MediaType.APPLICATION_JSON_VALUE );
						response.setCharacterEncoding ( StandardCharsets.UTF_8.name ( ) );
						
						GlobalExceptionResponseHandler error = new GlobalExceptionResponseHandler (
								Instant.now ( ) ,
								"Unauthorized" ,
								"Authentication is required to access this resource" ,
								HttpStatus.UNAUTHORIZED.value ( ) ,
								"UNAUTHORIZED" ,
								request.getRequestURI ( ) ,
								request.getMethod ( )
						);
						
						response.getWriter ( ).write ( objectMapper.writeValueAsString ( error ) );
					};
				}
				
				@Bean
				public SecurityFilterChain securityFilterChain ( HttpSecurity http ) throws Exception {
					http
							.cors ( cors -> cors
									                .configurationSource ( corsConfig.corsConfigurationSource ( ) )
							)
							.csrf ( AbstractHttpConfigurer::disable )
							.sessionManagement ( session -> session
									                                .sessionCreationPolicy ( SessionCreationPolicy.STATELESS )
							)
							.authorizeHttpRequests ( auth -> auth
									                                 .requestMatchers ( publicEndpoints ( ) ).permitAll ( )
									                                 
									                                 // ─── Authenticationand & Authorization Management endpoints ─────────────────────────────────────────────────────
																	 
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/auth/students" ).hasAnyRole ( "ADMIN" , "SUPER_ADMIN" )
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/auth/students/count" ).hasAnyRole ( "ADMIN" , "SUPER_ADMIN" )
									                                 .requestMatchers ( HttpMethod.DELETE , "/api/" + apiVersion + "/auth/students/**" ).hasAnyRole ( "ADMIN" , "SUPER_ADMIN" )
									                                 .requestMatchers ( HttpMethod.PATCH , "/api/" + apiVersion + "/auth/students/*/block" ).hasAnyRole ( "ADMIN" , "SUPER_ADMIN" , "PRINCIPAL" )
									                                 .requestMatchers ( HttpMethod.PATCH , "/api/" + apiVersion + "/auth/students/*/unblock" ).hasAnyRole ( "ADMIN" , "SUPER_ADMIN" , "PRINCIPAL" )
									                                 
									                                 // ─── Student Profile Management endpoints ─────────────────────────────────────────────────────
									                                 
									                                 .requestMatchers ( HttpMethod.POST , "/api/" + apiVersion + "/students/create-profile" )
									                                 .hasAnyRole ( "ADMIN" , "SUPER_ADMIN" , "STUDENT")
																	 
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/students/fetch-student-profiles" )
									                                 .hasAnyRole (
											                                 "ADMIN" , "SUPER_ADMIN" , "PRINCIPAL" , "VICE_PRINCIPAL" ,
											                                 "SCHOOL_DIRECTOR" , "SECRETARY" , "ACCOUNTANT" , "CASHIER" ,
											                                 "CLASS_TEACHER" , "TEACHER" , "HEAD_OF_DEPARTMENT"
									                                 )
																	 
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/students/student-profile/**" )
									                                 .hasAnyRole (
											                                 "ADMIN" , "SUPER_ADMIN" , "PRINCIPAL" , "VICE_PRINCIPAL" ,
											                                 "SCHOOL_DIRECTOR" , "SECRETARY" , "ACCOUNTANT" , "CASHIER" ,
											                                 "CLASS_TEACHER" , "TEACHER" , "HEAD_OF_DEPARTMENT", "STUDENT"
									                                 )
																	 
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/students/number/**" )
									                                 .hasAnyRole (
											                                 "ADMIN" , "SUPER_ADMIN" , "PRINCIPAL" , "VICE_PRINCIPAL" ,
											                                 "SCHOOL_DIRECTOR" , "SECRETARY" , "ACCOUNTANT" , "CASHIER" ,
											                                 "CLASS_TEACHER" , "TEACHER" , "HEAD_OF_DEPARTMENT"
									                                 )
									                                 
									                                 .requestMatchers ( HttpMethod.PUT , "/api/" + apiVersion + "/students/update-student-p/**" )
									                                 .hasAnyRole ( "ADMIN" , "SUPER_ADMIN", "STUDENT")
																	 
									                                 .requestMatchers ( HttpMethod.DELETE , "/api/" + apiVersion + "/students/delete-student-p/**" )
									                                 .hasAnyRole ( "ADMIN" , "SUPER_ADMIN")
									                         
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/students/count-student-profiles" )
									                                 .hasAnyRole (
											                                 "ADMIN" , "SUPER_ADMIN" , "PRINCIPAL" , "VICE_PRINCIPAL" ,
											                                 "SCHOOL_DIRECTOR" , "ACCOUNTANT"
									                                 )
																	 
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/students/count/class" )
									                                 .hasAnyRole (
											                                 "ADMIN" , "SUPER_ADMIN" , "PRINCIPAL" , "VICE_PRINCIPAL" ,
											                                 "SCHOOL_DIRECTOR" , "CLASS_TEACHER" , "HEAD_OF_DEPARTMENT"
									                                 )
																	 
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/students/search" )
									                                 .hasAnyRole (
											                                 "ADMIN" , "SUPER_ADMIN" , "PRINCIPAL" , "VICE_PRINCIPAL" ,
											                                 "SCHOOL_DIRECTOR" , "SECRETARY" , "ACCOUNTANT" , "CLASS_TEACHER" ,
											                                 "TEACHER" , "HEAD_OF_DEPARTMENT"
									                                 )
									                                 
									                  
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/students/fee-defaulters" )
									                                 .hasAnyRole (
											                                 "ADMIN" , "SUPER_ADMIN" , "ACCOUNTANT" , "CASHIER" ,
											                                 "PRINCIPAL" , "SCHOOL_DIRECTOR"
									                                 )
																	 
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/students/boarders" )
									                                 .hasAnyRole (
											                                 "ADMIN" , "SUPER_ADMIN" , "PRINCIPAL" , "VICE_PRINCIPAL" ,
											                                 "SCHOOL_DIRECTOR" , "SECRETARY"
									                                 )
																	 
									                                 .requestMatchers ( HttpMethod.GET , "/api/" + apiVersion + "/students/scholarships" )
									                                 .hasAnyRole (
											                                 "ADMIN" , "SUPER_ADMIN" , "ACCOUNTANT" , "PRINCIPAL" ,
											                                 "SCHOOL_DIRECTOR" , "SECRETARY"
									                                 )
									                                 
									                                 // ─── Payment Management endpoints ─────────────────────────────────────────────────────
																	 
									                                 .requestMatchers ( HttpMethod.POST , "/api/" + apiVersion + "/one-time-fee-payment" )
									                                 .hasAnyRole ( "ADMIN" , "SUPER_ADMIN" , "ACCOUNTANT" , "CASHIER" , "STUDENT" )
																	 
									                                
									                                 // ─── Courses Management  endpoints ─────────────────────────────────────────────────────
									                                 .requestMatchers(HttpMethod.POST,   "/api/" + apiVersion + "/courses/create-course")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PROFESSOR")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/courses/fetch-all-courses")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL",
											                                 "SCHOOL_DIRECTOR", "SECRETARY", "TEACHER", "CLASS_TEACHER","PROFESSOR",
											                                 "HEAD_OF_DEPARTMENT", "STUDENT")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/courses/search")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL", "PROFESSOR",
											                                 "SCHOOL_DIRECTOR", "TEACHER", "HEAD_OF_DEPARTMENT")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/courses/code/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "TEACHER", "PROFESSOR",
											                                 "CLASS_TEACHER", "HEAD_OF_DEPARTMENT", "STUDENT")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/courses/count")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "SCHOOL_DIRECTOR", "PROFESSOR")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/courses/count/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "SCHOOL_DIRECTOR", "PROFESSOR")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/courses/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL",
											                                 "SCHOOL_DIRECTOR", "SECRETARY", "TEACHER", "CLASS_TEACHER", "PROFESSOR",
											                                 "HEAD_OF_DEPARTMENT", "STUDENT")
									                                 
									                                 .requestMatchers(HttpMethod.PUT,    "/api/" + apiVersion + "/courses/update-course/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PROFESSOR")
									                                 
									                                 .requestMatchers(HttpMethod.DELETE, "/api/" + apiVersion + "/courses/delete-course/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PROFESSOR")
									                                 
									                                 // ─── Review endpoints ─────────────────────────────────────────────────────
									                                 .requestMatchers(HttpMethod.POST,   "/api/" + apiVersion + "/reviews/submit")
									                                 .hasAnyRole("STUDENT")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/reviews/fetch-all")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/reviews/course/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "TEACHER", "PROFESSOR",
											                                 "HEAD_OF_DEPARTMENT", "STUDENT")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/reviews/student/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "STUDENT")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/reviews/search")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "TEACHER", "PROFESSOR")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/reviews/count/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/reviews/average-rating/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "TEACHER", "PROFESSOR",
											                                 "PRINCIPAL", "SCHOOL_DIRECTOR", "STUDENT")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/reviews/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "TEACHER", "STUDENT")
									                                 
									                                 .requestMatchers(HttpMethod.PUT,    "/api/" + apiVersion + "/reviews/update/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "STUDENT")
									                                 
									                                 .requestMatchers(HttpMethod.DELETE, "/api/" + apiVersion + "/reviews/delete/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN")
																	 
									                                 // ─── Teacher Profile endpoints ────────────────────────────────────────────
									                                 .requestMatchers(HttpMethod.POST,   "/api/" + apiVersion + "/teacher-profiles/create-profile")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN",  "SECRETARY", "TEACHER", "PROFESSOR", "HEAD_OF_DEPARTMENT", "CLASS_TEACHER", "SUBSTITUTE_TEACHER", "TEACHING_ASSISTANT", "TUTOR")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/teacher-profiles/fetch-all")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL",
											                                 "SECRETARY", "HEAD_OF_DEPARTMENT", "TEACHER", "PROFESSOR", "CLASS_TEACHER", "SUBSTITUTE_TEACHER", "TEACHING_ASSISTANT", "TUTOR")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/teacher-profiles/search")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL", "SECRETARY", "TEACHER", "PROFESSOR",
											                                 "HEAD_OF_DEPARTMENT", "CLASS_TEACHER", "SUBSTITUTE_TEACHER", "TEACHING_ASSISTANT", "TUTOR")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/teacher-profiles/count/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL")
									                                 
									                                 .requestMatchers(HttpMethod.GET,    "/api/" + apiVersion + "/teacher-profiles/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL",
											                                  "SECRETARY", "TEACHER", "PROFESSOR", "CLASS_TEACHER", "SUBSTITUTE_TEACHER", "TEACHING_ASSISTANT", "TUTOR",
											                                 "HEAD_OF_DEPARTMENT")
									                                 
									                                 .requestMatchers(HttpMethod.PUT,    "/api/" + apiVersion + "/teacher-profiles/update/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "HEAD_OF_DEPARTMENT", "CLASS_TEACHER", "SUBSTITUTE_TEACHER", "TEACHING_ASSISTANT", "TUTOR")
									                                 
									                                 .requestMatchers(HttpMethod.DELETE, "/api/" + apiVersion + "/teacher-profiles/delete/**")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN")
									                                 
									                                 // ─── Attendance Management endpoints ─────────────────────────────────────────────────────
									                                 
									                                 .requestMatchers(HttpMethod.POST, "/api/" + apiVersion + "/attendance/create-attendance")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL", "TEACHER", "PROFESSOR", "CLASS_TEACHER")
									                                 
									                                 .requestMatchers(HttpMethod.GET, "/api/" + apiVersion + "/attendance/fetch-attendances")
									                                 .hasAnyRole(
											                                 "ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL", "SCHOOL_DIRECTOR",
											                                 "SECRETARY", "HEAD_OF_DEPARTMENT", "TEACHER", "PROFESSOR", "CLASS_TEACHER"
									                                 )
									                                 
									                                 .requestMatchers(HttpMethod.GET, "/api/" + apiVersion + "/attendance/fetch-attendance/{id}")
									                                 .hasAnyRole(
											                                 "ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL", "SCHOOL_DIRECTOR",
											                                 "SECRETARY", "HEAD_OF_DEPARTMENT", "TEACHER", "PROFESSOR", "CLASS_TEACHER"
									                                 )
																	 
									                                 
									                                 .requestMatchers(HttpMethod.GET, "/api/" + apiVersion + "/attendance/count")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL", "SCHOOL_DIRECTOR")
									                                 
									                                 .requestMatchers(HttpMethod.GET, "/api/" + apiVersion + "/attendance/update-attendance/{id}")
									                                 .hasAnyRole(
											                                 "ADMIN", "SUPER_ADMIN", "PRINCIPAL", "VICE_PRINCIPAL", "SCHOOL_DIRECTOR",
											                                 "TEACHER", "PROFESSOR", "CLASS_TEACHER", "STUDENT"
									                                 )
									                                 
									                                 .requestMatchers(HttpMethod.PATCH, "/api/" + apiVersion + "/attendance/delete-attendance/{id}")
									                                 .hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "TEACHER", "PROFESSOR", "CLASS_TEACHER")
																	 
									                                 .requestMatchers("/api/" + apiVersion + "/**").permitAll()
									                                 
									                                 // ─── All endpoints beneath must be authenticated  ─────────────────────────────────────────────────────
									                                 .anyRequest ().authenticated ()
							)
							.exceptionHandling ( exception -> exception
									                                  .accessDeniedHandler ( accessDeniedHandler ( ) )
									                                  .authenticationEntryPoint ( authenticationEntryPoint ( ) )
							)
							.addFilterBefore ( firebaseAuthenticationFilter , UsernamePasswordAuthenticationFilter.class )
							.addFilterBefore ( jwtAuthenticationFilter , FirebaseAuthenticationFilter.class );
					
					return http.build ( );
				}
}