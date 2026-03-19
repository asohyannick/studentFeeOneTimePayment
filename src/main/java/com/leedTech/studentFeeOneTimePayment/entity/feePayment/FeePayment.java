package com.leedTech.studentFeeOneTimePayment.entity.feePayment;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "fee_payments")
public class FeePayment {

		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		@Column(updatable = false, nullable = false)
		private UUID id;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(
				name = "student_profile_id",
				nullable = false,
				foreignKey = @ForeignKey(name = "fk_fee_payments_student_profile")
		)
		private StudentProfile studentProfile;
		
		@Column(name = "student_number", nullable = false, length = 20)
		private String studentNumber;
		
		@Column(name = "payment_amount", nullable = false, precision = 19, scale = 2)
		private BigDecimal paymentAmount;
		
		@Column(name = "previous_balance", nullable = false, precision = 19, scale = 2)
		private BigDecimal previousBalance;
		
		@Column(name = "incentive_rate", nullable = false, precision = 5, scale = 4)
		private BigDecimal incentiveRate;
		
		@Column(name = "incentive_amount", nullable = false, precision = 19, scale = 2)
		private BigDecimal incentiveAmount;
		
		@Column(name = "new_balance", nullable = false, precision = 19, scale = 2)
		private BigDecimal newBalance;
		
		@Column(name = "payment_date", nullable = false)
		private LocalDate paymentDate;
		
		@Column(name = "next_due_date", nullable = false)
		private LocalDate nextDueDate;
		
		@Column(name = "stripe_payment_intent_id", length = 255)
		private String stripePaymentIntentId;
		
		@Column(name = "stripe_payment_status", length = 50)
		private String stripePaymentStatus;
		
		@Column(name = "created_at", updatable = false, nullable = false)
		private Instant createdAt;
		
		@PrePersist
		protected void onCreate() {
			this.createdAt = Instant.now();
		}
}