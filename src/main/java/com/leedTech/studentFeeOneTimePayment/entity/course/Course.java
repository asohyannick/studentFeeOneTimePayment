package com.leedTech.studentFeeOneTimePayment.entity.course;

import com.leedTech.studentFeeOneTimePayment.constant.*;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Table(
		name = "courses",
		indexes = {
				@Index(columnList = "course_code",   name = "idx_courses_course_code"),
				@Index(columnList = "teacher_id",    name = "idx_courses_teacher_id"),
				@Index(columnList = "department",    name = "idx_courses_department"),
				@Index(columnList = "academic_year", name = "idx_courses_academic_year"),
				@Index(columnList = "course_status", name = "idx_courses_course_status"),
				@Index(columnList = "is_active",     name = "idx_courses_is_active"),
				@Index(columnList = "is_deleted",    name = "idx_courses_is_deleted")
		}
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
		
		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		private UUID id;
		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(
				name = "teacher_id",
				nullable = false,
				foreignKey = @ForeignKey(name = "fk_courses_teacher_id")
		)
		private User teacher;
		
		private String courseUrl;
		
		@Column(nullable = false)
		private String courseName;
		
		@Column(unique = true, nullable = false, length = 20)
		private String courseCode;
		
		@Column(columnDefinition = "TEXT")
		private String description;
		
		private String shortDescription;
		
		@Column(nullable = false)
		private Integer creditHours;
		
		private String courseObjectives;
		private String prerequisites;
		private String textbook;
		private String language;
		
		@Column(nullable = false)
		private String department;
		
		private String faculty;
		private String courseLevel;
		private String semester;
		
		@Column(nullable = false)
		private String academicYear;
		
		private LocalDate startDate;
		private LocalDate endDate;
		private Integer durationWeeks;
		
		@Enumerated(EnumType.STRING)
		private GradeLevelStatus gradeLevel;
		
		@Enumerated(EnumType.STRING)
		private ExamType examType;
		
		private String instructorName;
		private String instructorEmail;
		private String instructorPhone;
		private String coInstructorName;
		private String roomNumber;
		private String building;
		
		@Enumerated(EnumType.STRING)
		private ScheduleDayType scheduleDay;
		
		private LocalTime scheduleStartTime;
		private LocalTime scheduleEndTime;
		
		@Enumerated(EnumType.STRING)
		private ScheduleType scheduleType;
		
		private String meetingLink;
		
		@Column(name = "is_active", nullable = false)
		private boolean active = true;
		
		@Column(name = "is_deleted", nullable = false)
		private boolean deleted = false;
		
		@Column(name = "is_mandatory", nullable = false)
		private boolean mandatory = false;
		
		@Column(name = "is_elective", nullable = false)
		private boolean elective = false;
		
		@Column(nullable = false)
		private boolean allowLateEnrollment = false;
		
		@Column(nullable = false)
		private Integer maxCapacity;
		
		@Column(nullable = false)
		private Integer currentEnrollmentCount = 0;
		
		private Integer minEnrollmentCount;
		
		@Column(nullable = false)
		private Double passMark;
		
		@Column(nullable = false)
		private Double maxScore = 100.0;
		
		@Enumerated(EnumType.STRING)
		@Column(nullable = false)
		private CourseStatus courseStatus;
		
		private String notes;
		
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
			if (this.createdAt == null)              this.createdAt = now;
			if (this.updatedAt == null)              this.updatedAt = now;
			if (this.courseStatus == null)           this.courseStatus = CourseStatus.UPCOMING;
			if (this.gradeLevel == null)             this.gradeLevel = GradeLevelStatus.INTERMEDIATE;
			if (this.examType == null)               this.examType = ExamType.WRITTEN;
			if (this.scheduleDay == null)            this.scheduleDay = ScheduleDayType.TUESDAY;
			if (this.scheduleType == null)           this.scheduleType = ScheduleType.ONLINE;
			if (this.currentEnrollmentCount == null) this.currentEnrollmentCount = 0;
			if (this.maxScore == null)               this.maxScore = 100.0;
		}
		
		@PreUpdate
		protected void onUpdate() {
			this.updatedAt = Instant.now();
}
}