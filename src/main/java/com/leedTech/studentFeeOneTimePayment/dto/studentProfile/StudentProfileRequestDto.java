package com.leedTech.studentFeeOneTimePayment.dto.studentProfile;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "StudentProfileRequest", description = "Request payload for creating or updating a student profile")
public record StudentProfileRequestDto(
		
		@Schema(description = "UUID of the user account linked to this student profile", example = "a3f1c2d4-12ab-4e56-89cd-abcdef012345")
		@NotNull(message = "Student ID is required")
		UUID studentId,
		
		@Schema(description = "Unique student number", example = "STU-2026-00123")
		@NotBlank(message = "Student number is required")
		@Size(max = 20, message = "Student number must not exceed 20 characters")
		String studentNumber,
		
		@Schema(description = "National ID number", example = "CM123456789")
		@Size(max = 50, message = "National ID must not exceed 50 characters")
		String nationalId,
		
		@Schema(description = "Passport number", example = "PA1234567")
		@Size(max = 50, message = "Passport number must not exceed 50 characters")
		String passportNumber,
		
		@Schema(description = "Birth certificate number", example = "BC-2005-00456")
		@Size(max = 50, message = "Birth certificate number must not exceed 50 characters")
		String birthCertificateNumber,
		
		@Schema(description = "Date of birth", example = "2005-08-15")
		@NotNull(message = "Date of birth is required")
		@Past(message = "Date of birth must be in the past")
		LocalDate dateOfBirth,
		
		@Schema(description = "Gender of the student", example = "MALE")
		@NotNull(message = "Gender is required")
		Gender gender,
		
		@Schema(description = "Marital status", example = "SINGLE")
		MaritalStatus maritalStatus,
		
		@Schema(description = "Blood group", example = "O_POSITIVE")
		BloodGroup bloodGroup,
		
		@Schema(description = "Nationality", example = "Cameroonian")
		@Size(max = 100, message = "Nationality must not exceed 100 characters")
		String nationality,
		
		@Schema(description = "Nationality status", example = "CITIZEN")
		NationalityStatus nationalityStatus,
		
		@Schema(description = "Religion", example = "Christianity")
		@Size(max = 50, message = "Religion must not exceed 50 characters")
		String religion,
		
		@Schema(description = "Ethnicity", example = "Bamileke")
		@Size(max = 50, message = "Ethnicity must not exceed 50 characters")
		String ethnicity,
		
		@Schema(description = "Native language", example = "French")
		@Size(max = 50, message = "Native language must not exceed 50 characters")
		String nativeLanguage,
		
		@Schema(description = "Primary phone number", example = "+237612345678")
		@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Phone number must be valid")
		String phoneNumber,
		
		@Schema(description = "Alternative phone number", example = "+237698765432")
		@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Alternative phone number must be valid")
		String alternativePhoneNumber,
		
		@Schema(description = "Personal email address", example = "student@gmail.com")
		@Email(message = "Personal email must be valid")
		String personalEmail,
		
		@Schema(description = "Street address", example = "123 School Street")
		@Size(max = 255, message = "Street address must not exceed 255 characters")
		String streetAddress,
		
		@Schema(description = "City", example = "Yaounde")
		@Size(max = 100, message = "City must not exceed 100 characters")
		String city,
		
		@Schema(description = "State or province", example = "Centre")
		@Size(max = 100, message = "State or province must not exceed 100 characters")
		String stateOrProvince,
		
		@Schema(description = "Postal code", example = "00237")
		@Size(max = 20, message = "Postal code must not exceed 20 characters")
		String postalCode,
		
		@Schema(description = "Country", example = "Cameroon")
		@Size(max = 100, message = "Country must not exceed 100 characters")
		String country,
		
		@Schema(description = "Permanent address", example = "456 Permanent Ave, Douala, Cameroon")
		@Size(max = 500, message = "Permanent address must not exceed 500 characters")
		String permanentAddress,
		
		@Schema(description = "Current class", example = "Form 5")
		@Size(max = 50, message = "Current class must not exceed 50 characters")
		String currentClass,
		
		@Schema(description = "Current section", example = "A")
		@Size(max = 10, message = "Current section must not exceed 10 characters")
		String currentSection,
		
		@Schema(description = "Academic year", example = "2025-2026")
		@Size(max = 20, message = "Academic year must not exceed 20 characters")
		String academicYear,
		
		@Schema(description = "Admission date", example = "2020-09-01")
		LocalDate admissionDate,
		
		@Schema(description = "Graduation date", example = "2026-07-01")
		LocalDate graduationDate,
		
		@Schema(description = "Previous school name", example = "Government High School Yaounde")
		@Size(max = 255, message = "Previous school must not exceed 255 characters")
		String previousSchool,
		
		@Schema(description = "Previous school address", example = "Yaounde, Centre Region")
		@Size(max = 500, message = "Previous school address must not exceed 500 characters")
		String previousSchoolAddress,
		
		@Schema(description = "Current GPA", example = "3.75")
		@DecimalMin(value = "0.0", message = "GPA must be at least 0.0")
		@DecimalMax(value = "4.0", message = "GPA must not exceed 4.0")
		Double gpa,
		
		@Schema(description = "Enrollment status", example = "ACTIVE")
		EnrollmentStatus enrollmentStatus,
		
		@Schema(description = "Father's full name", example = "John Doe Sr.")
		@Size(max = 100, message = "Father name must not exceed 100 characters")
		String fatherName,
		
		@Schema(description = "Father's phone number", example = "+237677123456")
		@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Father phone must be valid")
		String fatherPhone,
		
		@Schema(description = "Father's email", example = "father@gmail.com")
		@Email(message = "Father email must be valid")
		String fatherEmail,
		
		@Schema(description = "Father's occupation", example = "Engineer")
		@Size(max = 100, message = "Father occupation must not exceed 100 characters")
		String fatherOccupation,
		
		@Schema(description = "Mother's full name", example = "Jane Doe")
		@Size(max = 100, message = "Mother name must not exceed 100 characters")
		String motherName,
		
		@Schema(description = "Mother's phone number", example = "+237699876543")
		@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Mother phone must be valid")
		String motherPhone,
		
		@Schema(description = "Mother's email", example = "mother@gmail.com")
		@Email(message = "Mother email must be valid")
		String motherEmail,
		
		@Schema(description = "Mother's occupation", example = "Nurse")
		@Size(max = 100, message = "Mother occupation must not exceed 100 characters")
		String motherOccupation,
		
		@Schema(description = "Guardian's full name", example = "Uncle Bob")
		@Size(max = 100, message = "Guardian name must not exceed 100 characters")
		String guardianName,
		
		@Schema(description = "Guardian's phone number", example = "+237655432109")
		@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Guardian phone must be valid")
		String guardianPhone,
		
		@Schema(description = "Guardian's email", example = "guardian@gmail.com")
		@Email(message = "Guardian email must be valid")
		String guardianEmail,
		
		@Schema(description = "Guardian's relationship to student", example = "Uncle")
		@Size(max = 50, message = "Guardian relationship must not exceed 50 characters")
		String guardianRelationship,
		
		@Schema(description = "Guardian's address", example = "789 Guardian Street, Yaounde")
		@Size(max = 500, message = "Guardian address must not exceed 500 characters")
		String guardianAddress,
		
		@Schema(description = "Emergency contact full name", example = "Alice Doe")
		@Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
		String emergencyContactName,
		
		@Schema(description = "Emergency contact phone", example = "+237611223344")
		@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Emergency contact phone must be valid")
		String emergencyContactPhone,
		
		@Schema(description = "Emergency contact email", example = "emergency@gmail.com")
		@Email(message = "Emergency contact email must be valid")
		String emergencyContactEmail,
		
		@Schema(description = "Emergency contact relationship", example = "Aunt")
		@Size(max = 50, message = "Emergency contact relationship must not exceed 50 characters")
		String emergencyContactRelationship,
		
		@Schema(description = "Known medical conditions", example = "Asthma")
		@Size(max = 1000, message = "Medical conditions must not exceed 1000 characters")
		String medicalConditions,
		
		@Schema(description = "Known allergies", example = "Peanuts, Dust")
		@Size(max = 500, message = "Allergies must not exceed 500 characters")
		String allergies,
		
		@Schema(description = "Current medications", example = "Ventolin inhaler")
		@Size(max = 500, message = "Medications must not exceed 500 characters")
		String medications,
		
		@Schema(description = "Disability information", example = "Hearing impairment")
		@Size(max = 500, message = "Disability must not exceed 500 characters")
		String disability,
		
		@Schema(description = "Special needs", example = "Requires front row seating")
		@Size(max = 500, message = "Special needs must not exceed 500 characters")
		String specialNeeds,
		
		@Schema(description = "Scholarship name if applicable", example = "Government Excellence Scholarship")
		@Size(max = 255, message = "Scholarship name must not exceed 255 characters")
		String scholarshipName,
		
		@Schema(description = "Scholarship percentage", example = "50.0")
		@DecimalMin(value = "0.0", message = "Scholarship percentage must be at least 0")
		@DecimalMax(value = "100.0", message = "Scholarship percentage must not exceed 100")
		Double scholarshipPercentage,
		
		@Schema(description = "Whether student is a fee defaulter", example = "false")
		boolean isFeeDefaulter,
		
		@Schema(description = "Discount percentage on fees", example = "10.0")
		@DecimalMin(value = "0.0", message = "Discount percentage must be at least 0")
		@DecimalMax(value = "100.0", message = "Discount percentage must not exceed 100")
		Double discountPercentage,
		
		@Schema(description = "Whether student uses school transport", example = "true")
		boolean usesSchoolTransport,
		
		@Schema(description = "Transport route", example = "Route 5 - Bastos")
		@Size(max = 255, message = "Transport route must not exceed 255 characters")
		String transportRoute,
		
		@Schema(description = "Bus stop name", example = "Bastos Junction")
		@Size(max = 255, message = "Bus stop must not exceed 255 characters")
		String busStop,
		
		@Schema(description = "Whether student is a boarder", example = "false")
		boolean isBoarder,
		
		@Schema(description = "Hostel name", example = "Block A Hostel")
		@Size(max = 100, message = "Hostel name must not exceed 100 characters")
		String hostelName,
		
		@Schema(description = "Room number", example = "A-204")
		@Size(max = 20, message = "Room number must not exceed 20 characters")
		String roomNumber,
		
		@Schema(description = "Student hobbies", example = "Football, Reading, Music")
		@Size(max = 500, message = "Hobbies must not exceed 500 characters")
		String hobbies,
		
		@Schema(description = "Extracurricular activities", example = "Debate club, Science club")
		@Size(max = 500, message = "Extracurricular activities must not exceed 500 characters")
		String extracurricularActivities,
		
		@Schema(description = "Additional notes", example = "Student requires extra attention in Mathematics")
		@Size(max = 1000, message = "Notes must not exceed 1000 characters")
		String notes
) {}