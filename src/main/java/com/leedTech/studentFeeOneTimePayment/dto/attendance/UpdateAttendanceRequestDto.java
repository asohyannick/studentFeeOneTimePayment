package com.leedTech.studentFeeOneTimePayment.dto.attendance;

import com.leedTech.studentFeeOneTimePayment.constant.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record UpdateAttendanceRequestDto(
		
		@NotNull(message = "Attendance ID is required")
		UUID attendanceId,
		
		LocalDate attendanceDate,
		
		LocalTime classStartTime,
		LocalTime classEndTime,
		LocalTime checkInTime,
		LocalTime checkOutTime,
		
		AttendanceStatus status,
		
		@Min(0)
		@Max(300)
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