package com.leedTech.studentFeeOneTimePayment.controller.feePaymentController;
import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.dto.payment.OneTimePaymentRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.payment.OneTimePaymentResponseDto;
import com.leedTech.studentFeeOneTimePayment.service.paymentService.FeePaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/${api.version}")
@RequiredArgsConstructor
@Tag(name = "Fee Payments API Endpoints", description = "Endpoints for processing one-time student fee payments via Stripe")
public class FeePaymentController {
private final FeePaymentService feePaymentService;

			@Operation(
					summary = "Process one-time fee payment",
					description = "Processes a one-time fee payment for a student, applies incentive credit, updates balance and calculates next due date via Stripe"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Payment processed successfully"),
					@ApiResponse(responseCode = "400", description = "Invalid payment amount or payment failed"),
					@ApiResponse(responseCode = "404", description = "Student or account not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@PostMapping("/one-time-fee-payment")
			public ResponseEntity<CustomResponseMessage<OneTimePaymentResponseDto>> processOneTimePayment(
					@Valid @RequestBody OneTimePaymentRequestDto request
			) {
				OneTimePaymentResponseDto data = feePaymentService.processOneTimePayment(request);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"One-time payment processed successfully. New balance: " + data.newBalance(),
								HttpStatus.OK.value(),
								data
						)
				);
			}
}