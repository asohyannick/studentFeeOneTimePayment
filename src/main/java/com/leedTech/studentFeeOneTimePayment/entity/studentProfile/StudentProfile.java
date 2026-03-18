package com.leedTech.studentFeeOneTimePayment.entity.studentProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
		name = "student_profiles",
		indexes = {
				@Index(columnList = "student_id",         name = "idx_student_profiles_student_id"),
				@Index(columnList = "student_number",     name = "idx_student_profiles_student_number"),
				@Index(columnList = "national_id",        name = "idx_student_profiles_national_id"),
				@Index(columnList = "current_class",      name = "idx_student_profiles_current_class"),
				@Index(columnList = "academic_year",      name = "idx_student_profiles_academic_year"),
				@Index(columnList = "enrollment_status",  name = "idx_student_profiles_enrollment_status")
		}
)
public class StudentProfile {
		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		@Column(updatable = false, nullable = false)
		private UUID id;
		
		@OneToOne(fetch = FetchType.LAZY)
		@JoinColumn(
				name = "student_id",
				nullable = false,
				unique = true,
				foreignKey = @ForeignKey(name = "fk_student_profiles_user")
		)
		private User student;
		
		@Column(name = "student_number", unique = true, nullable = false, length = 20)
		private String studentNumber;
		
		@Column(name = "profile_picture_url", length = 500)
		private String profilePictureUrl;
		
		@Column(name = "national_id", unique = true, length = 50)
		private String nationalId;
		
		@Column(name = "passport_number", unique = true, length = 50)
		private String passportNumber;
		
		@Column(name = "birth_certificate_number", unique = true, length = 50)
		private String birthCertificateNumber;
		
		@Column(name = "date_of_birth", nullable = false)
		private LocalDate dateOfBirth;
		
		@Enumerated(EnumType.STRING)
		@Column(name = "gender", nullable = false, length = 20)
		private Gender gender;
		
		@Enumerated(EnumType.STRING)
		@Column(name = "marital_status", length = 20)
		private MaritalStatus maritalStatus;
		
		@Enumerated(EnumType.STRING)
		@Column(name = "blood_group", length = 10)
		private BloodGroup bloodGroup;
		
		@Column(name = "nationality", length = 100)
		private String nationality;
		
		@Enumerated(EnumType.STRING)
		@Column(name = "nationality_status", length = 30)
		private NationalityStatus nationalityStatus;
		
		@Column(name = "religion", length = 50)
		private String religion;
		
		@Column(name = "ethnicity", length = 50)
		private String ethnicity;
		
		@Column(name = "native_language", length = 50)
		private String nativeLanguage;
		
		@Column(name = "phone_number", length = 20)
		private String phoneNumber;
		
		@Column(name = "alternative_phone_number", length = 20)
		private String alternativePhoneNumber;
		
		@Column(name = "personal_email", unique = true, length = 255)
		private String personalEmail;
		
		@Column(name = "street_address", length = 255)
		private String streetAddress;
		
		@Column(name = "city", length = 100)
		private String city;
		
		@Column(name = "state_or_province", length = 100)
		private String stateOrProvince;
		
		@Column(name = "postal_code", length = 20)
		private String postalCode;
		
		@Column(name = "country", length = 100)
		private String country;
		
		@Column(name = "permanent_address", length = 500)
		private String permanentAddress;
		
		@Column(name = "current_class", length = 50)
		private String currentClass;
		
		@Column(name = "current_section", length = 10)
		private String currentSection;
		
		@Column(name = "academic_year", length = 20)
		private String academicYear;
		
		@Column(name = "admission_date")
		private LocalDate admissionDate;
		
		@Column(name = "graduation_date")
		private LocalDate graduationDate;
		
		@Column(name = "previous_school", length = 255)
		private String previousSchool;
		
		@Column(name = "previous_school_address", length = 500)
		private String previousSchoolAddress;
		
		@Column(name = "gpa")
		private Double gpa;
		
		@Enumerated(EnumType.STRING)
		@Column(name = "enrollment_status", length = 30)
		private EnrollmentStatus enrollmentStatus;
		
		@Column(name = "father_name", length = 100)
		private String fatherName;
		
		@Column(name = "father_phone", length = 20)
		private String fatherPhone;
		
		@Column(name = "father_email", length = 255)
		private String fatherEmail;
		
		@Column(name = "father_occupation", length = 100)
		private String fatherOccupation;
		
		@Column(name = "mother_name", length = 100)
		private String motherName;
		
		@Column(name = "mother_phone", length = 20)
		private String motherPhone;
		
		@Column(name = "mother_email", length = 255)
		private String motherEmail;
		
		@Column(name = "mother_occupation", length = 100)
		private String motherOccupation;
		
		@Column(name = "guardian_name", length = 100)
		private String guardianName;
		
		@Column(name = "guardian_phone", length = 20)
		private String guardianPhone;
		
		@Column(name = "guardian_email", length = 255)
		private String guardianEmail;
		
		@Column(name = "guardian_relationship", length = 50)
		private String guardianRelationship;
		
		@Column(name = "guardian_address", length = 500)
		private String guardianAddress;
		
		@Column(name = "emergency_contact_name", length = 100)
		private String emergencyContactName;
		
		@Column(name = "emergency_contact_phone", length = 20)
		private String emergencyContactPhone;
		
		@Column(name = "emergency_contact_email", length = 255)
		private String emergencyContactEmail;
		
		@Column(name = "emergency_contact_relationship", length = 50)
		private String emergencyContactRelationship;
		
		@Column(name = "medical_conditions", length = 1000)
		private String medicalConditions;
		
		@Column(name = "allergies", length = 500)
		private String allergies;
		
		@Column(name = "medications", length = 500)
		private String medications;
		
		@Column(name = "disability", length = 500)
		private String disability;
		
		@Column(name = "special_needs", length = 500)
		private String specialNeeds;
		
		@Column(name = "scholarship_name", length = 255)
		private String scholarshipName;
		
		@Column(name = "scholarship_percentage")
		private Double scholarshipPercentage;
		
		@Builder.Default
		@Column(name = "is_fee_defaulter", nullable = false)
		private boolean isFeeDefaulter = false;
		
		@Column(name = "discount_percentage")
		private Double discountPercentage;
		
		@Builder.Default
		@Column(name = "uses_school_transport", nullable = false)
		private boolean usesSchoolTransport = false;
		
		@Column(name = "transport_route", length = 255)
		private String transportRoute;
		
		@Column(name = "bus_stop", length = 255)
		private String busStop;
		
		@Builder.Default
		@Column(name = "is_boarder", nullable = false)
		private boolean isBoarder = false;
		
		@Column(name = "hostel_name", length = 100)
		private String hostelName;
		
		@Column(name = "room_number", length = 20)
		private String roomNumber;
		
		@Column(name = "hobbies", length = 500)
		private String hobbies;
		
		@Column(name = "extracurricular_activities", length = 500)
		private String extracurricularActivities;
		
		@ElementCollection
		private List<String> completedCourses;
		
		@ElementCollection
		private List<String> interests;
		
		@ElementCollection
		private List<String> skills;
		
		@Column(name = "notes", length = 1000)
		private String notes;
		
		@Builder.Default
		@JsonIgnore
		@Column(name = "is_deleted", nullable = false)
		private boolean isDeleted = false;
		
		@JsonIgnore
		@Column(name = "deleted_at")
		private Instant deletedAt;
		
		@Column(name = "created_at", updatable = false, nullable = false)
		private Instant createdAt;
		
		@Column(name = "updated_at", nullable = false)
		private Instant updatedAt;
		
		@PrePersist
		protected void onCreate() {
			Instant now = Instant.now();
			this.createdAt       = now;
			this.updatedAt       = now;
			if (this.enrollmentStatus == null) {
				this.enrollmentStatus = EnrollmentStatus.ACTIVE;
			}
		}
		
		@PreUpdate
		protected void onUpdate() {
			this.updatedAt = Instant.now();
		}

}