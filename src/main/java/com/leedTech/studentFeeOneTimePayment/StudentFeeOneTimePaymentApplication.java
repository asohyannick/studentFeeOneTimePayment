package com.leedTech.studentFeeOneTimePayment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
@SpringBootApplication
@EnableCaching
public class StudentFeeOneTimePaymentApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudentFeeOneTimePaymentApplication.class, args);
	}
}


