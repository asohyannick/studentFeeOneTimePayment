package com.leedTech.studentFeeOneTimePayment.entity.assignment;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;
import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "assignment")
public class Assignment {

		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		private UUID id;
		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "teacher_id", nullable = false)
		private User teacher;
		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "student_id", nullable = false)
		private StudentProfile student;
		
		private String name;
		
		@Column(columnDefinition = "TEXT")
		private String instructions;
		
		private Instant availableFrom;
		private Instant deadline;
		private Double totalPoints;
		private BigDecimal passingScore;
		
		@Builder.Default
		private Integer maxAttempts = 1;
		
		private String category;
		private Boolean isGroupWork;
		private String attachmentUrl;
		private String gradingType;
		
		@Column(updatable = false)
		private Instant createdAt;
		private Instant updatedAt;
		
		@PrePersist
		protected void onCreate() {
			this.createdAt = Instant.now();
			this.updatedAt = Instant.now();
		}
		
		@PreUpdate
		protected void onUpdate() {
			this.updatedAt = Instant.now();
		}
}