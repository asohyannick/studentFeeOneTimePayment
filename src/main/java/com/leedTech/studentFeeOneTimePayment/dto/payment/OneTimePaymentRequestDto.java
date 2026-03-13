package com.leedTech.studentFeeOneTimePayment.dto.payment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Schema(name = "OneTimePaymentRequest")
public record OneTimePaymentRequestDto(
		
		@Schema(description = "Student number", example = "STU-2026-00123")
		@NotBlank(message = "Student number is required")
		String studentNumber,
		
		@Schema(description = "Payment amount (must be greater than 0)", example = "100000.00")
		@NotNull(message = "Payment amount is required")
		@DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
		BigDecimal paymentAmount,
		
		@Schema(description = "Payment date (defaults to today if not provided)", example = "2026-03-14")
		LocalDate paymentDate,
		
		@Schema(description = "Stripe payment method ID from frontend", example = "pm_card_visa")
		@NotBlank(message = "Payment method ID is required")
		String paymentMethodId,
		
		@Schema(description = "Currency code", example = "XAF")
		@NotBlank(message = "Currency is required")
		String currency
) {}