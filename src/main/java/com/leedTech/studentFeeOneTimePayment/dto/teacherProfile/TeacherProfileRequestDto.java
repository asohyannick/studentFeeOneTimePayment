package com.leedTech.studentFeeOneTimePayment.dto.teacherProfile;

import com.leedTech.studentFeeOneTimePayment.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TeacherProfileRequest", description = "Request payload for creating or updating a teacher profile")
public class TeacherProfileRequestDto {

			@Schema(description = "UUID of the user account linked to this teacher profile", example = "a3f1c2d4-12ab-4e56-89cd-abcdef012345")
			@NotNull(message = "Teacher ID is required")
			private UUID teacherId;
			
			@Schema(description = "Unique employee ID", example = "TCH-2026-001")
			@Size(max = 20, message = "Employee ID must not exceed 20 characters")
			private String employeeId;
			
			@Schema(description = "Profile picture — upload as multipart file")
			private MultipartFile profilePictureFile;
			
			@Schema(description = "Profile picture URL if providing a link instead of uploading", example = "https://example.com/photo.jpg")
			@Pattern(regexp = "^$|^(https?://).*", message = "Profile picture URL must be a valid URL")
			private String profilePictureUrl;
			
			@Schema(description = "Primary phone number", example = "+237612345678")
			@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Phone number must be valid")
			private String phoneNumber;
			
			@Schema(description = "Alternative phone number", example = "+237698765432")
			@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Alternative phone number must be valid")
			private String alternativePhoneNumber;
			
			@Schema(description = "Personal email address", example = "teacher@gmail.com")
			@Email(message = "Personal email must be valid")
			private String personalEmail;
			
			@Schema(description = "Street address", example = "123 Teacher Street")
			@Size(max = 255, message = "Street address must not exceed 255 characters")
			private String streetAddress;
			
			@Schema(description = "City", example = "Yaounde")
			@Size(max = 100, message = "City must not exceed 100 characters")
			private String city;
			
			@Schema(description = "State or province", example = "Centre")
			@Size(max = 100, message = "State or province must not exceed 100 characters")
			private String stateOrProvince;
			
			@Schema(description = "Postal code", example = "00237")
			@Size(max = 20, message = "Postal code must not exceed 20 characters")
			private String postalCode;
			
			@Schema(description = "Country", example = "Cameroon")
			@Size(max = 100, message = "Country must not exceed 100 characters")
			private String country;
			
			@Schema(description = "Date of birth", example = "1985-06-15")
			@Past(message = "Date of birth must be in the past")
			private LocalDate dateOfBirth;
			
			@Schema(description = "Gender", example = "MALE")
			private Gender gender;
			
			@Schema(description = "Marital status", example = "MARRIED")
			private MaritalStatus maritalStatus;
			
			@Schema(description = "Nationality", example = "Cameroonian")
			@Size(max = 100, message = "Nationality must not exceed 100 characters")
			private String nationality;
			
			@Schema(description = "National ID number", example = "CM123456789")
			@Size(max = 50, message = "National ID must not exceed 50 characters")
			private String nationalId;
			
			@Schema(description = "Passport number", example = "PA1234567")
			@Size(max = 50, message = "Passport number must not exceed 50 characters")
			private String passportNumber;
			
			@Schema(description = "Religion", example = "Christianity")
			@Size(max = 50, message = "Religion must not exceed 50 characters")
			private String religion;
			
			@Schema(description = "Ethnicity", example = "Bamileke")
			@Size(max = 50, message = "Ethnicity must not exceed 50 characters")
			private String ethnicity;
			
			@Schema(description = "Native language", example = "French")
			@Size(max = 50, message = "Native language must not exceed 50 characters")
			private String nativeLanguage;
			
			@Schema(description = "Emergency contact full name", example = "Jane Doe")
			@Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
			private String emergencyContactName;
			
			@Schema(description = "Emergency contact phone", example = "+237611223344")
			@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Emergency contact phone must be valid")
			private String emergencyContactPhone;
			
			@Schema(description = "Emergency contact relationship", example = "Spouse")
			@Size(max = 50, message = "Emergency contact relationship must not exceed 50 characters")
			private String emergencyContactRelationship;
			
			@Schema(description = "Department", example = "Mathematics")
			@NotBlank(message = "Department is required")
			@Size(max = 100, message = "Department must not exceed 100 characters")
			private String department;
			
			@Schema(description = "Faculty", example = "Science & Technology")
			@Size(max = 100, message = "Faculty must not exceed 100 characters")
			private String faculty;
			
			@Schema(description = "Area of specialization", example = "Organic Chemistry")
			@Size(max = 255, message = "Specialization must not exceed 255 characters")
			private String specialization;
			
			@Schema(description = "Highest qualification", example = "PhD")
			@Size(max = 100, message = "Highest qualification must not exceed 100 characters")
			private String highestQualification;
			
			@Schema(description = "Qualification details", example = "PhD in Mathematics, University of Yaounde I, 2015")
			private String qualificationDetails;
			
			@Schema(description = "Years of teaching experience", example = "10")
			@Min(value = 0, message = "Years of experience must be at least 0")
			@Max(value = 60, message = "Years of experience must not exceed 60")
			private Integer yearsOfExperience;
			
			@Schema(description = "Previous employer", example = "Government High School Yaounde")
			@Size(max = 255, message = "Previous employer must not exceed 255 characters")
			private String previousEmployer;
			
			@Schema(description = "Skills", example = "Python, Research, Data Analysis")
			private String skills;
			
			@Schema(description = "Professional certifications", example = "CISCO Certified, Cambridge CELTA")
			private String certifications;
			
			@Schema(description = "Publications", example = "Applied Mathematics Vol. 3, 2020")
			private String publications;
			
			@Schema(description = "Awards", example = "Best Teacher Award 2022")
			private String awards;
			
			@Schema(description = "Professional biography")
			private String bio;
			
			@Schema(description = "LinkedIn profile URL", example = "https://linkedin.com/in/johndoe")
			@Pattern(regexp = "^$|^(https?://).*", message = "LinkedIn URL must be valid")
			private String linkedInProfile;
			
			@Schema(description = "Research interests", example = "Number Theory, Algebra")
			private String researchInterests;
			
			@Schema(description = "Teacher type", example = "FULL_TIME")
			private TeacherType teacherType;
			
			@Schema(description = "Subjects taught", example = "Mathematics, Physics")
			private String subjectsTaught;
			
			@Schema(description = "Classes assigned", example = "Form 4A, Form 5B")
			private String classesAssigned;
			
			@Schema(description = "Academic year", example = "2025-2026")
			@Size(max = 20, message = "Academic year must not exceed 20 characters")
			private String academicYear;
			
			@Schema(description = "Current schedule", example = "Mon/Wed/Fri 08:00-10:00")
			@Size(max = 255, message = "Current schedule must not exceed 255 characters")
			private String currentSchedule;
			
			@Schema(description = "Preferred schedule day", example = "MONDAY")
			private ScheduleDayType preferredScheduleDay;
			
			@Schema(description = "Maximum weekly teaching hours", example = "20")
			@Min(value = 1, message = "Max weekly hours must be at least 1")
			@Max(value = 60, message = "Max weekly hours must not exceed 60")
			private Integer maxWeeklyHours;
			
			@Schema(description = "Current weekly teaching hours", example = "18")
			@Min(value = 0, message = "Current weekly hours must be at least 0")
			private Integer currentWeeklyHours;
			
			@Schema(description = "Office room number", example = "A-101")
			@Size(max = 20, message = "Office room must not exceed 20 characters")
			private String officeRoom;
			
			@Schema(description = "Office hours", example = "Mon 14:00-16:00, Wed 10:00-12:00")
			@Size(max = 100, message = "Office hours must not exceed 100 characters")
			private String officeHours;
			
			@Schema(description = "Classroom building", example = "Block A")
			@Size(max = 100, message = "Classroom building must not exceed 100 characters")
			private String classroomBuilding;
			
			// ─── Employment ───────────────────────────────────────────────────────────
			@Schema(description = "Date of joining the institution", example = "2018-09-01")
			private LocalDate joinDate;
			
			@Schema(description = "Contract start date", example = "2024-09-01")
			private LocalDate contractStartDate;
			
			@Schema(description = "Contract end date", example = "2026-08-31")
			private LocalDate contractEndDate;
			
			@Schema(description = "Employment type", example = "FULL_TIME")
			private EmploymentType employmentType;
			
			@Schema(description = "Employment status", example = "ACTIVE")
			private EmploymentStatus employmentStatus;
			
			@Schema(description = "Monthly salary", example = "350000.0")
			@DecimalMin(value = "0.0", message = "Salary must be at least 0")
			private Double salary;
			
			@Schema(description = "Salary grade", example = "Grade 7")
			@Size(max = 50, message = "Salary grade must not exceed 50 characters")
			private String salaryGrade;
			
			@Schema(description = "Bank name", example = "Afriland First Bank")
			@Size(max = 100, message = "Bank name must not exceed 100 characters")
			private String bankName;
			
			@Schema(description = "Bank account number", example = "01234567890")
			@Size(max = 50, message = "Bank account number must not exceed 50 characters")
			private String bankAccountNumber;
			
			@Schema(description = "Tax identification number", example = "TIN123456789")
			@Size(max = 50, message = "Tax identification number must not exceed 50 characters")
			private String taxIdentificationNumber;
			
			@Schema(description = "Social security number", example = "SSN987654321")
			@Size(max = 50, message = "Social security number must not exceed 50 characters")
			private String socialSecurityNumber;
			
			@Schema(description = "Total annual leave days", example = "30")
			@Min(value = 0, message = "Annual leave days must be at least 0")
			private Integer annualLeaveDays;
			
			@Schema(description = "Used leave days so far", example = "5")
			@Min(value = 0, message = "Used leave days must be at least 0")
			private Integer usedLeaveDays;
			
			@Schema(description = "HR or admin notes")
			@Size(max = 1000, message = "Notes must not exceed 1000 characters")
			private String notes;
}