package com.leedTech.studentFeeOneTimePayment.entity.attendance;

import com.leedTech.studentFeeOneTimePayment.constant.*;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
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
		name = "attendances",
		indexes = {
				@Index(columnList = "student_id",    name = "idx_attendances_student_id"),
				@Index(columnList = "course_id",     name = "idx_attendances_course_id"),
				@Index(columnList = "marked_by_id",  name = "idx_attendances_marked_by_id"),
				@Index(columnList = "attendance_date", name = "idx_attendances_date"),
				@Index(columnList = "status",        name = "idx_attendances_status"),
				@Index(columnList = "academic_year", name = "idx_attendances_academic_year")
		},
		uniqueConstraints = {
				@UniqueConstraint(
						columnNames = {"student_id", "course_id", "attendance_date"},
						name = "uk_attendances_student_course_date"
				)
		}
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

			@Id
			@GeneratedValue(strategy = GenerationType.UUID)
			private UUID id;
			
			@ManyToOne(fetch = FetchType.LAZY)
			@JoinColumn(
					name = "student_id",
					nullable = false,
					foreignKey = @ForeignKey(name = "fk_attendances_student_id")
			)
			private User student;
			
			@ManyToOne(fetch = FetchType.LAZY)
			@JoinColumn(
					name = "course_id",
					nullable = false,
					foreignKey = @ForeignKey(name = "fk_attendances_course_id")
			)
			private Course course;
			
			@ManyToOne(fetch = FetchType.LAZY)
			@JoinColumn(
					name = "marked_by_id",
					nullable = false,
					foreignKey = @ForeignKey(name = "fk_attendances_marked_by_id")
			)
			private User markedBy;
			
			@Column(nullable = false)
			private LocalDate attendanceDate;
			
			private LocalTime classStartTime;
			private LocalTime classEndTime;
			private LocalTime checkInTime;
			private LocalTime checkOutTime;
			
			@Enumerated(EnumType.STRING)
			@Column(nullable = false)
			private AttendanceStatus status;
			
			@Column(nullable = false)
			private String academicYear;
			
			private String semester;
			
			private String currentClass;
			
			private String subject;
			
			private Integer lateMinutes;
			
			private String absenceReason;
			
			@Enumerated(EnumType.STRING)
			private AbsenceType absenceType;
			
			private boolean excusedByParent;
			private boolean excusedByAdmin;
			private String excuseNote;
			
			@Enumerated(EnumType.STRING)
			private SessionType sessionType;
			
			private Integer sessionNumber;
			
			private String roomNumber;
			
			private String building;
			
			@Enumerated(EnumType.STRING)
			private ScheduleDayType dayOfWeek;
			
			@Enumerated(EnumType.STRING)
			private AttendanceMethod method;
			
			private boolean verified;
			
			private String verificationNote;
			
			@Column(name = "is_deleted", nullable = false)
			private boolean deleted = false;
			
			private Instant deletedAt;
			
			@Column(columnDefinition = "TEXT")
			private String remarks;
			
			@CreationTimestamp
			@Column(updatable = false, nullable = false)
			private Instant createdAt;
			
			@UpdateTimestamp
			@Column(nullable = false)
			private Instant updatedAt;
			
			// ─── Lifecycle Hooks ──────────────────────────────────────────────────────
			@PrePersist
			protected void onCreate() {
				Instant now = Instant.now();
				if (this.createdAt == null) this.createdAt = now;
				if (this.updatedAt == null) this.updatedAt = now;
				if (this.status == null)    this.status    = AttendanceStatus.ABSENT;
				if (this.method == null)    this.method    = AttendanceMethod.MANUAL;
			}
			
			@PreUpdate
			protected void onUpdate() {
				this.updatedAt = Instant.now();
			}
}