package com.leedTech.studentFeeOneTimePayment.entity.studentAccount;
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
@Table(name = "student_accounts")
public class StudentAccount {

		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		@Column(updatable = false, nullable = false)
		private UUID id;
		
		@OneToOne(fetch = FetchType.LAZY)
		@JoinColumn(
				name = "student_profile_id",
				nullable = false,
				unique = true,
				foreignKey = @ForeignKey(name = "fk_student_accounts_profile")
		)
		private StudentProfile studentProfile;
		
		@Column(name = "balance", nullable = false, precision = 19, scale = 2)
		private BigDecimal balance;
		
		@Column(name = "next_due_date")
		private LocalDate nextDueDate;
		
		@Column(name = "created_at", updatable = false, nullable = false)
		private Instant createdAt;
		
		@Column(name = "updated_at", nullable = false)
		private Instant updatedAt;
		
		@PrePersist
		protected void onCreate() {
			Instant now = Instant.now();
			this.createdAt = now;
			this.updatedAt = now;
		}
		
		@PreUpdate
		protected void onUpdate() {
			this.updatedAt = Instant.now();
		}
}