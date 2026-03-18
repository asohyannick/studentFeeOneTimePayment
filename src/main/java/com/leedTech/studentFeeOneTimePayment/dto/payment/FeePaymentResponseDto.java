package com.leedTech.studentFeeOneTimePayment.dto.payment;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FeePaymentResponseDto(
		UUID        id,
		String      studentNumber,
		BigDecimal  paymentAmount,
		BigDecimal  previousBalance,
		BigDecimal  incentiveRate,
		BigDecimal  incentiveAmount,
		BigDecimal  newBalance,
		LocalDate   paymentDate,
		LocalDate   nextDueDate,
		String      stripePaymentIntentId,
		String      stripePaymentStatus,
		Instant     createdAt
) {}