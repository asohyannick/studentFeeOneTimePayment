package com.leedTech.studentFeeOneTimePayment.dto.course;
import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
@Schema(name = "CourseTeacher", description = "Basic teacher info embedded in course response")
public record CourseTeacherDto(
		
		@Schema(description = "Teacher's user ID", example = "a3f1c2d4-12ab-4e56-89cd-abcdef012345")
		UUID id,
		
		@Schema(description = "Teacher's first name", example = "James")
		String firstName,
		
		@Schema(description = "Teacher's last name", example = "Brown")
		String lastName,
		
		@Schema(description = "Teacher's full name", example = "James Brown")
		String fullName,
		
		@Schema(description = "Teacher's email", example = "james.brown@leedtech.com")
		String email,
		
		@Schema(description = "Teacher's role", example = "TEACHER")
		UserRole role
) {}