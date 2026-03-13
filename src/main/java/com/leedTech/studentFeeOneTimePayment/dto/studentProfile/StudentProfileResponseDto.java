package com.leedTech.studentFeeOneTimePayment.dto.studentProfile;

import com.leedTech.studentFeeOneTimePayment.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "StudentProfileResponse", description = "Response payload for student profile data")
public record StudentProfileResponseDto(
		
		@Schema(description = "Unique profile ID")
		UUID id,
		
		@Schema(description = "Linked user account ID")
		UUID studentId,
		
		@Schema(description = "Student's first name")
		String firstName,
		
		@Schema(description = "Student's last name")
		String lastName,
		
		@Schema(description = "Student's account email")
		String email,
		
		@Schema(description = "Student number")
		String studentNumber,
		
		@Schema(description = "Profile picture URL")
		String profilePictureUrl,
		
		@Schema(description = "National ID")
		String nationalId,
		
		@Schema(description = "Passport number")
		String passportNumber,
		
		@Schema(description = "Birth certificate number")
		String birthCertificateNumber,
		
		@Schema(description = "Date of birth")
		LocalDate dateOfBirth,
		
		@Schema(description = "Gender")
		Gender gender,
		
		@Schema(description = "Marital status")
		MaritalStatus maritalStatus,
		
		@Schema(description = "Blood group")
		BloodGroup bloodGroup,
		
		@Schema(description = "Nationality")
		String nationality,
		
		@Schema(description = "Nationality status")
		NationalityStatus nationalityStatus,
		
		@Schema(description = "Religion")
		String religion,
		
		@Schema(description = "Ethnicity")
		String ethnicity,
		
		@Schema(description = "Native language")
		String nativeLanguage,
		
		@Schema(description = "Phone number")
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
		
		@Schema(description = "Permanent address")
		String permanentAddress,
		
		@Schema(description = "Current class")
		String currentClass,
		
		@Schema(description = "Current section")
		String currentSection,
		
		@Schema(description = "Academic year")
		String academicYear,
		
		@Schema(description = "Admission date")
		LocalDate admissionDate,
		
		@Schema(description = "Graduation date")
		LocalDate graduationDate,
		
		@Schema(description = "Previous school")
		String previousSchool,
		
		@Schema(description = "Previous school address")
		String previousSchoolAddress,
		
		@Schema(description = "GPA")
		Double gpa,
		
		@Schema(description = "Enrollment status")
		EnrollmentStatus enrollmentStatus,
		
		@Schema(description = "Father's name")
		String fatherName,
		
		@Schema(description = "Father's phone")
		String fatherPhone,
		
		@Schema(description = "Father's email")
		String fatherEmail,
		
		@Schema(description = "Father's occupation")
		String fatherOccupation,
		
		@Schema(description = "Mother's name")
		String motherName,
		
		@Schema(description = "Mother's phone")
		String motherPhone,
		
		@Schema(description = "Mother's email")
		String motherEmail,
		
		@Schema(description = "Mother's occupation")
		String motherOccupation,
		
		@Schema(description = "Guardian's name")
		String guardianName,
		
		@Schema(description = "Guardian's phone")
		String guardianPhone,
		
		@Schema(description = "Guardian's email")
		String guardianEmail,
		
		@Schema(description = "Guardian's relationship")
		String guardianRelationship,
		
		@Schema(description = "Guardian's address")
		String guardianAddress,
		
		@Schema(description = "Emergency contact name")
		String emergencyContactName,
		
		@Schema(description = "Emergency contact phone")
		String emergencyContactPhone,
		
		@Schema(description = "Emergency contact email")
		String emergencyContactEmail,
		
		@Schema(description = "Emergency contact relationship")
		String emergencyContactRelationship,
		
		@Schema(description = "Medical conditions")
		String medicalConditions,
		
		@Schema(description = "Allergies")
		String allergies,
		
		@Schema(description = "Medications")
		String medications,
		
		@Schema(description = "Disability")
		String disability,
		
		@Schema(description = "Special needs")
		String specialNeeds,
		
		@Schema(description = "Scholarship name")
		String scholarshipName,
		
		@Schema(description = "Scholarship percentage")
		Double scholarshipPercentage,
		
		@Schema(description = "Is fee defaulter")
		boolean isFeeDefaulter,
		
		@Schema(description = "Discount percentage")
		Double discountPercentage,
		
		@Schema(description = "Uses school transport")
		boolean usesSchoolTransport,
		
		@Schema(description = "Transport route")
		String transportRoute,
		
		@Schema(description = "Bus stop")
		String busStop,
		
		@Schema(description = "Is boarder")
		boolean isBoarder,
		
		@Schema(description = "Hostel name")
		String hostelName,
		
		@Schema(description = "Room number")
		String roomNumber,
		
		@Schema(description = "Hobbies")
		String hobbies,
		
		@Schema(description = "Extracurricular activities")
		String extracurricularActivities,
		
		@Schema(description = "Notes")
		String notes,
		
		@Schema(description = "Record creation timestamp")
		Instant createdAt,
		
		@Schema(description = "Record last update timestamp")
		Instant updatedAt
) {}