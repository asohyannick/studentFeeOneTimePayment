package com.leedTech.studentFeeOneTimePayment.service.paymentService;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class StripePaymentService {

@Value("${stripe.api-key}")
private String stripeApiKey;

@PostConstruct
public void init() {
	Stripe.apiKey = stripeApiKey;
}

// ─── Step 1: Create PaymentIntent ────────────────────────────────
public PaymentIntent createPaymentIntent(BigDecimal amount, String currency, String studentNumber) {
	try {
		long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
		
		PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
				                                   .setAmount(amountInCents)
				                                   .setCurrency(currency.toLowerCase())
				                                   .setDescription("One-time fee payment for student: " + studentNumber)
				                                   .putMetadata("student_number", studentNumber)
				                                   .setAutomaticPaymentMethods(
						                                   PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
								                                   .setEnabled(true)
								                                   .setAllowRedirects(
										                                   PaymentIntentCreateParams.AutomaticPaymentMethods
												                                   .AllowRedirects.NEVER
								                                   )
								                                   .build()
				                                   )
				                                   .build();
		
		PaymentIntent intent = PaymentIntent.create(params);
		log.info("PaymentIntent created: {} for student: {}", intent.getId(), studentNumber);
		return intent;
		
	} catch (StripeException e) {
		log.error("Failed to create PaymentIntent: {}", e.getMessage());
		throw new BadRequestException("Payment processing failed: " + e.getMessage());
	}
}

// ─── Step 2: Confirm PaymentIntent ───────────────────────────────
public PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId) {
	try {
		PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
		
		PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
				                                    .setPaymentMethod(paymentMethodId)
				                                    .build();
		
		PaymentIntent confirmed = intent.confirm(params);
		log.info("PaymentIntent confirmed: {} status: {}", confirmed.getId(), confirmed.getStatus());
		return confirmed;
		
	} catch (StripeException e) {
		log.error("Failed to confirm PaymentIntent: {}", e.getMessage());
		throw new BadRequestException("Payment confirmation failed: " + e.getMessage());
	}
}

// ─── Retrieve PaymentIntent ──────────────────────────────────────
public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
	try {
		return PaymentIntent.retrieve(paymentIntentId);
	} catch (StripeException e) {
		throw new BadRequestException("Failed to retrieve payment: " + e.getMessage());
	}
}
}