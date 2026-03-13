package com.leedTech.studentFeeOneTimePayment.dto.studentProfile;
import com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus;
import com.leedTech.studentFeeOneTimePayment.constant.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "StudentProfileSummary", description = "Lightweight student profile for list views")
public record StudentProfileSummaryDto(
		UUID id,
		UUID studentId,
		String firstName,
		String lastName,
		String email,
		String studentNumber,
		String profilePictureUrl,
		Gender gender,
		String currentClass,
		String currentSection,
		String academicYear,
		LocalDate dateOfBirth,
		String phoneNumber,
		EnrollmentStatus enrollmentStatus,
		boolean isFeeDefaulter,
		boolean isBoarder,
		boolean usesSchoolTransport
) {}