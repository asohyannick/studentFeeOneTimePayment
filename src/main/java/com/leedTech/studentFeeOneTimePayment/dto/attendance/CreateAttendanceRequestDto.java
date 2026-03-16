package com.leedTech.studentFeeOneTimePayment.dto.attendance;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateAttendanceRequestDto(
		
		@NotNull(message = "Student ID is required")
		@Schema(description = "ID of the student", example = "2d9a8d2c-2b2c-4f3a-8b6a-1c4d5e6f7a8b")
		UUID studentId,
		
		@NotNull(message = "Course ID is required")
		@Schema(description = "ID of the course", example = "8d7a6f5e-4c3b-2a1d-9f8e-7d6c5b4a3e2f")
		UUID courseId,
		
		@NotNull(message = "Marked by user ID is required")
		UUID markedById,
		
		@NotNull(message = "Attendance date is required")
		@Schema(description = "Date of attendance", example = "2026-03-16")
		LocalDate attendanceDate,
		
		LocalTime classStartTime,
		LocalTime classEndTime,
		LocalTime checkInTime,
		LocalTime checkOutTime,
		
		@NotNull(message = "Attendance status is required")
		AttendanceStatus status,
		
		@NotBlank(message = "Academic year is required")
		@Size(max = 20)
		String academicYear,
		
		@Size(max = 20)
		String semester,
		
		@Size(max = 50)
		String currentClass,
		
		@Size(max = 100)
		String subject,
		
		@Min(value = 0, message = "Late minutes cannot be negative")
		@Max(value = 300, message = "Late minutes cannot exceed 300")
		Integer lateMinutes,
		
		@Size(max = 255)
		String absenceReason,
		
		AbsenceType absenceType,
		
		Boolean excusedByParent,
		Boolean excusedByAdmin,
		
		@Size(max = 255)
		String excuseNote,
		
		SessionType sessionType,
		
		@Min(1)
		@Max(10)
		Integer sessionNumber,
		
		@Size(max = 20)
		String roomNumber,
		
		@Size(max = 50)
		String building,
		
		ScheduleDayType dayOfWeek,
		
		AttendanceMethod method,
		
		Boolean verified,
		
		@Size(max = 255)
		String verificationNote,
		
		@Size(max = 1000)
		String remarks
) {}