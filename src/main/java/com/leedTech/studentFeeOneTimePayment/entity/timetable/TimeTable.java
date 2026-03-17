package com.leedTech.studentFeeOneTimePayment.entity.timetable;

import com.leedTech.studentFeeOneTimePayment.constant.Term;
import com.leedTech.studentFeeOneTimePayment.constant.TimetableStatus;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;
@Entity
@Table(
		name = "timetable",
		indexes = {
				@Index(columnList = "day_of_week",    name = "idx_timetable_day_of_week"),
				@Index(columnList = "academic_year",  name = "idx_timetable_academic_year"),
				@Index(columnList = "term",           name = "idx_timetable_term"),
				@Index(columnList = "class_name",     name = "idx_timetable_class_name"),
				@Index(columnList = "teacher_id",     name = "idx_timetable_teacher_id"),
				@Index(columnList = "course_id",      name = "idx_timetable_course_id")
		}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeTable {

			@Id
			@GeneratedValue(strategy = GenerationType.UUID)
			@Column(updatable = false, nullable = false)
			private UUID id;
			
			@Enumerated(EnumType.STRING)
			@Column(name = "day_of_week", nullable = false, length = 20)
			private DayOfWeek dayOfWeek;
			
			@Column(name = "start_time", nullable = false)
			private LocalTime startTime;
			
			@Column(name = "end_time", nullable = false)
			private LocalTime endTime;
			
			@Column(name = "period_number")
			private Integer periodNumber;
			
			@Column(name = "duration_minutes")
			private Integer durationMinutes;
			
			@Enumerated(EnumType.STRING)
			@Column(name = "status", length = 30)
			@Builder.Default
			private TimetableStatus status = TimetableStatus.ACTIVE;
			
			@ManyToOne(fetch = FetchType.LAZY)
			@JoinColumn(
					name = "course_id",
					nullable = false,
					foreignKey = @ForeignKey(name = "fk_timetable_course")
			)
			private Course course;
			
			@ManyToOne(fetch = FetchType.LAZY)
			@JoinColumn(
					name = "teacher_id",
					nullable = false,
					foreignKey = @ForeignKey(name = "fk_timetable_teacher")
			)
			private User teacher;
			
			@Column(name = "class_name", nullable = false, length = 50)
			private String className;
			
			@Column(name = "section", length = 20)
			private String section;
			
			@Column(name = "room_number", length = 30)
			private String roomNumber;
			
			@Column(name = "building", length = 100)
			private String building;
			
			@Column(name = "floor", length = 20)
			private String floor;
			
			@Column(name = "room_capacity")
			private Integer roomCapacity;
			
			@Column(name = "academic_year", nullable = false, length = 20)
			private String academicYear;
			
			@Enumerated(EnumType.STRING)
			@Column(name = "term", nullable = false, length = 20)
			private Term term;
			
			@Column(name = "effective_from")
			private Instant effectiveFrom;
			
			@Column(name = "effective_to")
			private Instant effectiveTo;
			
			@Column(name = "notes", columnDefinition = "TEXT")
			private String notes;
			
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