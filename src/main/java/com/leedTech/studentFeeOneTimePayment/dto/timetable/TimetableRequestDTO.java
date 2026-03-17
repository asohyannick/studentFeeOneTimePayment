package com.leedTech.studentFeeOneTimePayment.dto.timetable;

import com.leedTech.studentFeeOneTimePayment.constant.Term;
import com.leedTech.studentFeeOneTimePayment.constant.TimetableStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record TimetableRequestDTO(
		
		@NotNull(message = "Day of week is required")
		DayOfWeek dayOfWeek,
		
		@NotNull(message = "Start time is required")
		LocalTime startTime,
		
		@NotNull(message = "End time is required")
		LocalTime endTime,
		
		Integer periodNumber,
		
		Integer durationMinutes,
		
		TimetableStatus status,
		
		@NotNull(message = "Course ID is required")
		UUID courseId,
		
		@NotNull(message = "Teacher ID is required")
		UUID teacherId,
		
		@NotBlank(message = "Class name is required")
		@Size(max = 50)
		String className,
		
		@Size(max = 20)
		String section,
		
		@Size(max = 30)
		String roomNumber,
		
		@Size(max = 100)
		String building,
		
		@Size(max = 20)
		String floor,
		
		@Positive(message = "Room capacity must be positive")
		Integer roomCapacity,
		
		@NotBlank(message = "Academic year is required")
		@Size(max = 20)
		String academicYear,
		
		@NotNull(message = "Term is required")
		Term term,
		
		Instant effectiveFrom,
		
		Instant effectiveTo,
		
		String notes

) {
	public TimetableRequestDTO {
		if (status == null) status = TimetableStatus.ACTIVE;
	}
}