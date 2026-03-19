package com.leedTech.studentFeeOneTimePayment.entity.review;
import com.leedTech.studentFeeOneTimePayment.constant.ReviewStatus;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
		name = "reviews",
		indexes = {
				@Index(columnList = "course_id",  name = "idx_reviews_course_id"),
				@Index(columnList = "student_id", name = "idx_reviews_student_id"),
				@Index(columnList = "approved",   name = "idx_reviews_approved"),
				@Index(columnList = "status",     name = "idx_reviews_status"),
				@Index(columnList = "deleted",    name = "idx_reviews_deleted"),
				@Index(columnList = "flagged",    name = "idx_reviews_flagged")
		}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		private UUID id;
		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(
				name = "course_id",
				nullable = false,
				foreignKey = @ForeignKey(name = "fk_reviews_course_id")
		)
		private Course course;
		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(
				name = "student_id",
				nullable = false,
				foreignKey = @ForeignKey(name = "fk_reviews_student_id")
		)
		private User student;
		
		@Column(nullable = false)
		private int rating;
		
		@Column(nullable = false, columnDefinition = "TEXT")
		private String comment;
		
		private String title;
		
		@Column(columnDefinition = "TEXT")
		private String pros;
		
		@Column(columnDefinition = "TEXT")
		private String cons;
		
		private boolean recommended;
		
		private int helpfulCount;
		private int reportCount;
		
		@Enumerated(EnumType.STRING)
		@Column(nullable = false)
		private ReviewStatus status = ReviewStatus.PENDING;
		
		private boolean approved;
		private boolean deleted;
		private boolean flagged;
		
		private String rejectionReason;
		
		@Column(columnDefinition = "TEXT")
		private String adminNote;

		@Column(name = "is_edited")
		private boolean edited;
		
		private Instant editedAt;
		@CreationTimestamp
		@Column(updatable = false, nullable = false)
		private Instant createdAt;
		
		@UpdateTimestamp
		@Column(nullable = false)
		private Instant updatedAt;
		
		private Instant deletedAt;
		
		// ─── Lifecycle Hooks ──────────────────────────────────────────────────────
		@PrePersist
		protected void onCreate() {
			Instant now = Instant.now();
			if (this.createdAt == null) this.createdAt = now;
			if (this.updatedAt == null) this.updatedAt = now;
			if (this.status == null)    this.status    = ReviewStatus.PENDING;
			if (this.helpfulCount == 0) this.helpfulCount = 0;
			if (this.reportCount  == 0) this.reportCount  = 0;
		}
		
		@PreUpdate
		protected void onUpdate() {
			this.updatedAt = Instant.now();
		}
}