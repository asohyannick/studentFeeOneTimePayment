package com.leedTech.studentFeeOneTimePayment.dto.attendance;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Schema(description = "Attendance response payload")
public record AttendanceResponseDto(
		
		@Schema(description = "Attendance ID")
		UUID id,
		
		@Schema(description = "Student ID")
		UUID studentId,
		
		@Schema(description = "Student full name", example = "John Doe")
		String studentName,
		
		@Schema(description = "Course ID")
		UUID courseId,
		
		@Schema(description = "Course name", example = "Mathematics")
		String courseName,
		
		@Schema(description = "ID of the staff who marked attendance")
		UUID markedById,
		
		@Schema(description = "Name of the staff who marked attendance")
		String markedByName,
		
		@Schema(description = "Date of attendance")
		LocalDate attendanceDate,
		
		LocalTime classStartTime,
		LocalTime classEndTime,
		LocalTime checkInTime,
		LocalTime checkOutTime,
		
		AttendanceStatus status,
		
		String academicYear,
		String semester,
		String currentClass,
		String subject,
		
		Integer lateMinutes,
		
		String absenceReason,
		
		AbsenceType absenceType,
		
		Boolean excusedByParent,
		Boolean excusedByAdmin,
		
		String excuseNote,
		
		SessionType sessionType,
		Integer sessionNumber,
		
		String roomNumber,
		String building,
		
		ScheduleDayType dayOfWeek,
		
		AttendanceMethod method,
		
		Boolean verified,
		
		String verificationNote,
		
		String remarks,
		
		@Schema(description = "Record creation timestamp")
		Instant createdAt,
		
		@Schema(description = "Record last update timestamp")
		Instant updatedAt
) {}
