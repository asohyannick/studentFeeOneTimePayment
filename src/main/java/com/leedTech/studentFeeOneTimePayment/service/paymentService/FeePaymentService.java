package com.leedTech.studentFeeOneTimePayment.service.paymentService;

import com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus;
import com.leedTech.studentFeeOneTimePayment.dto.payment.FeePaymentResponseDto;
import com.leedTech.studentFeeOneTimePayment.dto.payment.OneTimePaymentRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.payment.OneTimePaymentResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.feePayment.FeePayment;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.repository.feePaymentRepository.FeePaymentRepository;
import com.leedTech.studentFeeOneTimePayment.repository.studentProfileRepository.StudentProfileRepository;
import com.leedTech.studentFeeOneTimePayment.utils.studentFeeOneTimePayment.DueDateCalculator;
import com.leedTech.studentFeeOneTimePayment.utils.studentFeeOneTimePayment.IncentiveCalculator;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeePaymentService {

		private final StudentProfileRepository studentProfileRepository;
		private final FeePaymentRepository feePaymentRepository;
		private final StripePaymentService   stripePaymentService;
		
		@Transactional
		public OneTimePaymentResponseDto processOneTimePayment( OneTimePaymentRequestDto request) {
			
			LocalDate paymentDate = request.paymentDate() != null
					                        ? request.paymentDate()
					                        : LocalDate.now();
			
			StudentProfile profile = studentProfileRepository
					                         .findByStudentNumber(request.studentNumber())
					                         .orElseThrow(() -> new NotFoundException (
							                         "Student not found with number: " + request.studentNumber()
							                         +  ". Please create your student profile before making a payment."
					                         ));
			
		
			if (profile.getEnrollmentStatus() != EnrollmentStatus.ACTIVE) {
				throw new BadRequestException (
						"Student account is not active. Status: " + profile.getEnrollmentStatus()
				);
			}
			
			if (request.paymentAmount().compareTo( BigDecimal.ZERO) <= 0) {
				throw new BadRequestException("Payment amount must be greater than 0");
			}
			
			// 4. Calculate incentives
			BigDecimal previousBalance = BigDecimal.ZERO; // no balance tracking without StudentAccount
			BigDecimal incentiveRate   = IncentiveCalculator.getIncentiveRate(request.paymentAmount());
			BigDecimal incentiveAmount = IncentiveCalculator.calculateIncentiveAmount(request.paymentAmount());
			BigDecimal newBalance      = IncentiveCalculator.calculateNewBalance(
					previousBalance, request.paymentAmount(), incentiveAmount
			);
			
			LocalDate nextDueDate = DueDateCalculator.calculateNextDueDate(paymentDate);
			
			// 5. Process Stripe payment
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
			
			FeePayment payment = FeePayment.builder()
					                     .studentProfile(profile)
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

		@Transactional(readOnly = true)
		public Page < FeePaymentResponseDto > fetchAllPayments( Pageable pageable) {
			return feePaymentRepository
					       .findAllPayments(pageable)
					       .map(this::toResponseDto);
		}
		
		@Transactional(readOnly = true)
		public List <FeePaymentResponseDto> fetchPaymentsByStudentNumber( String studentNumber) {
			
			if (!studentProfileRepository.existsByStudentNumber(studentNumber)) {
				throw new NotFoundException(
						"Student not found with number: " + studentNumber
				);
			}
			
			return feePaymentRepository
					       .findAllByStudentNumber(studentNumber)
					       .stream()
					       .map(this::toResponseDto)
					       .toList();
		}
		
		private FeePaymentResponseDto toResponseDto(FeePayment payment) {
			return new FeePaymentResponseDto(
					payment.getId(),
					payment.getStudentNumber(),
					payment.getPaymentAmount(),
					payment.getPreviousBalance(),
					payment.getIncentiveRate(),
					payment.getIncentiveAmount(),
					payment.getNewBalance(),
					payment.getPaymentDate(),
					payment.getNextDueDate(),
					payment.getStripePaymentIntentId(),
					payment.getStripePaymentStatus(),
					payment.getCreatedAt()
			);
		}
}