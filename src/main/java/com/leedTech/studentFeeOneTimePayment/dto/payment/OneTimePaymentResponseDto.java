package com.leedTech.studentFeeOneTimePayment.dto.payment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
@Schema(name = "OneTimePaymentResponse")
public record OneTimePaymentResponseDto(
		
		@Schema(description = "Student number")
		String studentNumber,
		
		@Schema(description = "Previous balance before payment")
		BigDecimal previousBalance,
		
		@Schema(description = "Amount paid")
		BigDecimal paymentAmount,
		
		@Schema(description = "Incentive rate applied", example = "0.03")
		BigDecimal incentiveRate,
		
		@Schema(description = "Incentive amount credited")
		BigDecimal incentiveAmount,
		
		@Schema(description = "New balance after payment and incentive")
		BigDecimal newBalance,
		
		@Schema(description = "Next payment due date")
		LocalDate nextDueDate,
		
		@Schema(description = "Stripe payment intent ID")
		String stripePaymentIntentId,
		
		@Schema(description = "Stripe payment status")
		String stripePaymentStatus
) {}