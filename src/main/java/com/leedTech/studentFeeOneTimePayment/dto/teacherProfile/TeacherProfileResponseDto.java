package com.leedTech.studentFeeOneTimePayment.dto.teacherProfile;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TeacherProfileResponse", description = "Response payload for teacher profile data")
public record TeacherProfileResponseDto(
		
		@Schema(description = "Unique profile ID")
		UUID id,
		
		@Schema(description = "Linked user account ID")
		UUID teacherId,
		
		@Schema(description = "Teacher first name")
		String firstName,
		
		@Schema(description = "Teacher last name")
		String lastName,
		
		@Schema(description = "Teacher full name")
		String fullName,
		
		@Schema(description = "Teacher account email")
		String email,
		
		@Schema(description = "Teacher role")
		String role,
		
		@Schema(description = "Unique employee ID")
		String employeeId,
		
		@Schema(description = "Profile picture URL")
		String profilePictureUrl,
		
		@Schema(description = "Primary phone number")
		String phoneNumber,
		
		@Schema(description = "Alternative phone number")
		String alternativePhoneNumber,
		
		@Schema(description = "Personal email")
		String personalEmail,
		
		@Schema(description = "Street address")
		String streetAddress,
		
		@Schema(description = "City")
		String city,
		
		@Schema(description = "State or province")
		String stateOrProvince,
		
		@Schema(description = "Postal code")
		String postalCode,
		
		@Schema(description = "Country")
		String country,
		
		@Schema(description = "Date of birth")
		LocalDate dateOfBirth,
		
		@Schema(description = "Gender")
		Gender gender,
		
		@Schema(description = "Marital status")
		MaritalStatus maritalStatus,
		
		@Schema(description = "Nationality")
		String nationality,
		
		@Schema(description = "National ID")
		String nationalId,
		
		@Schema(description = "Passport number")
		String passportNumber,
		
		@Schema(description = "Religion")
		String religion,
		
		@Schema(description = "Ethnicity")
		String ethnicity,
		
		@Schema(description = "Native language")
		String nativeLanguage,
		
		@Schema(description = "Emergency contact name")
		String emergencyContactName,
		
		@Schema(description = "Emergency contact phone")
		String emergencyContactPhone,
		
		@Schema(description = "Emergency contact relationship")
		String emergencyContactRelationship,
		
		@Schema(description = "Department")
		String department,
		
		@Schema(description = "Faculty")
		String faculty,
		
		@Schema(description = "Specialization")
		String specialization,
		
		@Schema(description = "Highest qualification")
		String highestQualification,
		
		@Schema(description = "Qualification details")
		String qualificationDetails,
		
		@Schema(description = "Years of experience")
		Integer yearsOfExperience,
		
		@Schema(description = "Previous employer")
		String previousEmployer,
		
		@Schema(description = "Skills")
		String skills,
		
		@Schema(description = "Certifications")
		String certifications,
		
		@Schema(description = "Publications")
		String publications,
		
		@Schema(description = "Awards")
		String awards,
		
		@Schema(description = "Professional biography")
		String bio,
		
		@Schema(description = "LinkedIn profile URL")
		String linkedInProfile,
		
		@Schema(description = "Research interests")
		String researchInterests,
		
		@Schema(description = "Teacher type")
		TeacherType teacherType,
		
		@Schema(description = "Subjects taught")
		String subjectsTaught,
		
		@Schema(description = "Classes assigned")
		String classesAssigned,
		
		@Schema(description = "Academic year")
		String academicYear,
		
		@Schema(description = "Current schedule")
		String currentSchedule,
		
		@Schema(description = "Preferred schedule day")
		ScheduleDayType preferredScheduleDay,
		
		@Schema(description = "Maximum weekly hours")
		Integer maxWeeklyHours,
		
		@Schema(description = "Current weekly hours")
		Integer currentWeeklyHours,
		
		@Schema(description = "Office room")
		String officeRoom,
		
		@Schema(description = "Office hours")
		String officeHours,
		
		@Schema(description = "Classroom building")
		String classroomBuilding,
		
		@Schema(description = "Join date")
		LocalDate joinDate,
		
		@Schema(description = "Contract start date")
		LocalDate contractStartDate,
		
		@Schema(description = "Contract end date")
		LocalDate contractEndDate,
		
		@Schema(description = "Employment type")
		EmploymentType employmentType,
		
		@Schema(description = "Employment status")
		EmploymentStatus employmentStatus,
		
		@Schema(description = "Monthly salary")
		Double salary,
		
		@Schema(description = "Salary grade")
		String salaryGrade,
		
		@Schema(description = "Bank name")
		String bankName,
		
		@Schema(description = "Bank account number")
		String bankAccountNumber,
		
		@Schema(description = "Tax identification number")
		String taxIdentificationNumber,
		
		@Schema(description = "Social security number")
		String socialSecurityNumber,
		
		@Schema(description = "Annual leave days")
		Integer annualLeaveDays,
		
		@Schema(description = "Used leave days")
		Integer usedLeaveDays,
		
		@Schema(description = "Remaining leave days")
		Integer remainingLeaveDays,
		
		@Schema(description = "Notes")
		String notes,
		
		@Schema(description = "Is profile active")
		boolean active,
		
		@Schema(description = "Is credentials verified")
		boolean verified,
		
		@Schema(description = "Is account blocked")
		boolean blocked,
		
		@Schema(description = "Is currently on leave")
		boolean onLeave,
		
		@Schema(description = "Record creation timestamp")
		Instant createdAt,
		
		@Schema(description = "Record last updated timestamp")
		Instant updatedAt

) {}