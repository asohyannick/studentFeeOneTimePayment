package com.leedTech.studentFeeOneTimePayment.service.paymentService;

import com.leedTech.studentFeeOneTimePayment.dto.payment.OneTimePaymentRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.payment.OneTimePaymentResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.feePayment.FeePayment;
import com.leedTech.studentFeeOneTimePayment.entity.studentAccount.StudentAccount;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.repository.feePaymentRepository.FeePaymentRepository;
import com.leedTech.studentFeeOneTimePayment.repository.studentAccountRepository.StudentAccountRepository;
import com.leedTech.studentFeeOneTimePayment.repository.studentProfileRepository.StudentProfileRepository;
import com.leedTech.studentFeeOneTimePayment.utils.studentFeeOneTimePayment.DueDateCalculator;
import com.leedTech.studentFeeOneTimePayment.utils.studentFeeOneTimePayment.IncentiveCalculator;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeePaymentService {

private final StudentProfileRepository  studentProfileRepository;
private final StudentAccountRepository  studentAccountRepository;
private final FeePaymentRepository      feePaymentRepository;
private final StripePaymentService      stripePaymentService;

@Transactional
public OneTimePaymentResponseDto processOneTimePayment(OneTimePaymentRequestDto request) {
	
	LocalDate paymentDate = request.paymentDate() != null
			                        ? request.paymentDate()
			                        : LocalDate.now();
	
	StudentProfile profile = studentProfileRepository
			                         .findByStudentNumber(request.studentNumber())
			                         .orElseThrow(() -> new NotFoundException(
					                         "Student not found with number: " + request.studentNumber()
			                         ));
	
	StudentAccount account = studentAccountRepository
			                         .findByStudentProfile(profile)
			                         .orElseThrow(() -> new NotFoundException(
					                         "No account found for student: " + request.studentNumber()
			                         ));
	
	if (request.paymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
		throw new BadRequestException("Payment amount must be greater than 0");
	}
	if (request.paymentAmount().compareTo(account.getBalance()) > 0) {
		throw new BadRequestException("Payment amount exceeds current balance of: " + account.getBalance());
	}
	
	BigDecimal previousBalance  = account.getBalance();
	BigDecimal incentiveRate    = IncentiveCalculator.getIncentiveRate(request.paymentAmount());
	BigDecimal incentiveAmount  = IncentiveCalculator.calculateIncentiveAmount(request.paymentAmount());
	BigDecimal newBalance       = IncentiveCalculator.calculateNewBalance(
			previousBalance, request.paymentAmount(), incentiveAmount
	);
	
	LocalDate nextDueDate = DueDateCalculator.calculateNextDueDate(paymentDate);
	
	PaymentIntent paymentIntent = stripePaymentService.createPaymentIntent(
			request.paymentAmount(),
			request.currency(),
			request.studentNumber()
	);
	
	PaymentIntent confirmedIntent = stripePaymentService.confirmPaymentIntent(
			paymentIntent.getId(),
			request.paymentMethodId()
	);
	
	if (!"succeeded".equals(confirmedIntent.getStatus())) {
		throw new BadRequestException(
				"Payment failed with status: " + confirmedIntent.getStatus()
		);
	}
	
	account.setBalance(newBalance);
	account.setNextDueDate(nextDueDate);
	studentAccountRepository.save(account);
	
	FeePayment payment = FeePayment.builder()
			                     .studentAccount(account)
			                     .studentNumber(request.studentNumber())
			                     .paymentAmount(request.paymentAmount())
			                     .previousBalance(previousBalance)
			                     .incentiveRate(incentiveRate)
			                     .incentiveAmount(incentiveAmount)
			                     .newBalance(newBalance)
			                     .paymentDate(paymentDate)
			                     .nextDueDate(nextDueDate)
			                     .stripePaymentIntentId(confirmedIntent.getId())
			                     .stripePaymentStatus(confirmedIntent.getStatus())
			                     .build();
	
	feePaymentRepository.save(payment);
	log.info("One-time payment processed for student: {}, amount: {}",
			request.studentNumber(), request.paymentAmount());
	
	return new OneTimePaymentResponseDto(
			request.studentNumber(),
			previousBalance,
			request.paymentAmount(),
			incentiveRate,
			incentiveAmount,
			newBalance,
			nextDueDate,
			confirmedIntent.getId(),
			confirmedIntent.getStatus()
	);
}
}