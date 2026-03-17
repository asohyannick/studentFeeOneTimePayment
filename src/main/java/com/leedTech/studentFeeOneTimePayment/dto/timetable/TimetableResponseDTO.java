package com.leedTech.studentFeeOneTimePayment.dto.timetable;

import com.leedTech.studentFeeOneTimePayment.constant.Term;
import com.leedTech.studentFeeOneTimePayment.constant.TimetableStatus;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record TimetableResponseDTO(
		
		UUID id,
		
		DayOfWeek dayOfWeek,
		LocalTime startTime,
		LocalTime endTime,
		Integer periodNumber,
		Integer durationMinutes,
		TimetableStatus status,
		
		UUID courseId,
		String courseName,
		String courseCode,
		UUID teacherId,
		String teacherName,
		
		String className,
		String section,
		String roomNumber,
		String building,
		String floor,
		Integer roomCapacity,
		
		String academicYear,
		Term term,
		Instant effectiveFrom,
		Instant effectiveTo,
		
		String notes,
		
		Instant createdAt,
		Instant updatedAt
) {}