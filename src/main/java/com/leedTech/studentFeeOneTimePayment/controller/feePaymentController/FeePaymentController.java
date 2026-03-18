package com.leedTech.studentFeeOneTimePayment.controller.feePaymentController;

import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.dto.payment.FeePaymentResponseDto;
import com.leedTech.studentFeeOneTimePayment.dto.payment.OneTimePaymentRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.payment.OneTimePaymentResponseDto;
import com.leedTech.studentFeeOneTimePayment.service.paymentService.FeePaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${api.version}/payment")
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
			
			@Operation(
					summary = "Fetch all payment records",
					description = "Returns a paginated list of all fee payment records. " +
							              "**Allowed roles:** ADMIN, SUPER_ADMIN, ACCOUNTANT, CASHIER, PRINCIPAL"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Payments fetched successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden")
			})
			@GetMapping ("/fetch-all-payments")
			public ResponseEntity<CustomResponseMessage<Page< FeePaymentResponseDto >>> fetchAllPayments(
					@RequestParam(defaultValue = "0")  int page,
					@RequestParam(defaultValue = "10") int size,
					@RequestParam(defaultValue = "createdAt") String sortBy,
					@RequestParam(defaultValue = "desc") String sortDir) {
				
				Pageable pageable = PageRequest.of(
						page, size,
						sortDir.equalsIgnoreCase("asc")
								? Sort.by(sortBy).ascending()
								: Sort.by(sortBy).descending()
				);
				
				Page <FeePaymentResponseDto> data = feePaymentService.fetchAllPayments(pageable);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Payments fetched successfully. Total: " + data.getTotalElements(),
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Fetch payment records by student number",
					description = "Returns all payment records for a specific student. " +
							              "**Allowed roles:** ADMIN, SUPER_ADMIN, ACCOUNTANT, CASHIER, STUDENT"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Payments fetched successfully"),
					@ApiResponse(responseCode = "404", description = "Student not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden")
			})
			@GetMapping("/fetch-payments/{studentNumber}")
			public ResponseEntity<CustomResponseMessage<List<FeePaymentResponseDto>>> fetchPaymentsByStudent(
					@PathVariable String studentNumber) {
				
				List <FeePaymentResponseDto> data = feePaymentService.fetchPaymentsByStudentNumber(studentNumber);
				
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Payments fetched successfully for student: " + studentNumber,
								HttpStatus.OK.value(),
								data
						)
				);
			}
}