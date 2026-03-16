package com.leedTech.studentFeeOneTimePayment.entity.teacherProfile;

import com.leedTech.studentFeeOneTimePayment.constant.*;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Table(
		name = "teacher_profiles",
		indexes = {
				@Index(columnList = "teacher_id",   name = "idx_teacher_profiles_teacher_id"),
				@Index(columnList = "department",    name = "idx_teacher_profiles_department"),
				@Index(columnList = "is_active",     name = "idx_teacher_profiles_is_active"),
				@Index(columnList = "is_deleted",    name = "idx_teacher_profiles_is_deleted"),
				@Index(columnList = "employee_id",   name = "idx_teacher_profiles_employee_id")
		},
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "teacher_id",  name = "uk_teacher_profiles_teacher_id"),
				@UniqueConstraint(columnNames = "employee_id", name = "uk_teacher_profiles_employee_id"),
				@UniqueConstraint(columnNames = "national_id", name = "uk_teacher_profiles_national_id")
		}
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfile {

			@Id
			@GeneratedValue(strategy = GenerationType.UUID)
			private UUID id;
			
			@OneToOne(fetch = FetchType.LAZY)
			@JoinColumn(
					name = "teacher_id",
					nullable = false,
					foreignKey = @ForeignKey(name = "fk_teacher_profiles_teacher_id")
			)
			private User teacher;
			
			@Column(unique = true)
			private String employeeId;
			
			private String profilePictureUrl;
			
			private String phoneNumber;
			private String alternativePhoneNumber;
			
			private String personalEmail;
			
			private String streetAddress;
			private String city;
			private String stateOrProvince;
			private String postalCode;
			private String country;
			
			private LocalDate dateOfBirth;
			
			@Enumerated(EnumType.STRING)
			private Gender gender;
			
			@Enumerated(EnumType.STRING)
			private MaritalStatus maritalStatus;
			
			private String nationality;
			private String nationalId;
			private String passportNumber;
			
			private String religion;
			private String ethnicity;
			private String nativeLanguage;
			
			private String emergencyContactName;
			private String emergencyContactPhone;
			private String emergencyContactRelationship;
			
			@Column(nullable = false)
			private String department;
			
			private String faculty;
			
			private String specialization;
			
			private String highestQualification;
			
			private String qualificationDetails;
			
			private Integer yearsOfExperience;
			
			private String previousEmployer;
			
			private String skills;
			
			private String certifications;
			
			private String publications;
			
			private String awards;
			
			@Column(columnDefinition = "TEXT")
			private String bio;
			
			private String linkedInProfile;
			
			private String researchInterests;
			
			@Enumerated(EnumType.STRING)
			private TeacherType teacherType;
			
			private String subjectsTaught;
			
			private String classesAssigned;
			
			private String academicYear;
			
			private String currentSchedule;
			
			@Enumerated(EnumType.STRING)
			private ScheduleDayType preferredScheduleDay;
			
			private Integer maxWeeklyHours;
			
			private Integer currentWeeklyHours;
			
			private String officeRoom;
			
			private String officeHours;
			
			private String classroomBuilding;
			
			private LocalDate joinDate;
			
			private LocalDate contractStartDate;
			
			private LocalDate contractEndDate;
			
			@Enumerated(EnumType.STRING)
			private EmploymentType employmentType;
			@Enumerated(EnumType.STRING)
			private EmploymentStatus employmentStatus;
			
			private Double salary;
			
			private String salaryGrade;
			
			private String bankName;
			
			private String bankAccountNumber;
			
			private String taxIdentificationNumber;
			
			private String socialSecurityNumber;
			
			private Integer annualLeaveDays;
			
			private Integer usedLeaveDays;
			
			private String notes;
			
			@Column(name = "is_active", nullable = false)
			private boolean active = true;
			
			@Column(name = "is_deleted", nullable = false)
			private boolean deleted = false;
			
			@Column(name = "is_verified", nullable = false)
			private boolean verified = false;
			
			@Column(name = "is_blocked", nullable = false)
			private boolean blocked = false;
			
			@Column(name = "is_on_leave", nullable = false)
			private boolean onLeave = false;
			
			@CreationTimestamp
			@Column(updatable = false, nullable = false)
			private Instant createdAt;
			
			@UpdateTimestamp
			@Column(nullable = false)
			private Instant updatedAt;
			
			private Instant deletedAt;
			
			private Instant blockedAt;
			
			@PrePersist
			protected void onCreate() {
				Instant now = Instant.now();
				if (this.createdAt == null) this.createdAt = now;
				if (this.updatedAt == null) this.updatedAt = now;
				if (this.teacherType == null)      this.teacherType      = TeacherType.FULL_TIME;
				if (this.employmentType == null)   this.employmentType   = EmploymentType.FULL_TIME;
				if (this.employmentStatus == null) this.employmentStatus = EmploymentStatus.ACTIVE;
				if (this.annualLeaveDays == null)  this.annualLeaveDays  = 30;
				if (this.usedLeaveDays == null)    this.usedLeaveDays    = 0;
			}
			
			@PreUpdate
			protected void onUpdate() {
				this.updatedAt = Instant.now();
			}
}