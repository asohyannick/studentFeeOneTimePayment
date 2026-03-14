package com.leedTech.studentFeeOneTimePayment.dto.course;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Schema(name = "CourseResponse", description = "Response payload for course data")
public record CourseResponseDto(
		
		@Schema(description = "Unique course ID")
		UUID id,
		
		@Schema(description = "Teacher who owns this course")
		CourseTeacherDto teacher,
		
		@Schema(description = "Course name")
		String courseName,
		
		@Schema(description = "Unique course code")
		String courseCode,
		
		@Schema(description = "Course URL or uploaded image URL")
		String courseUrl,
		
		@Schema(description = "Full course description")
		String description,
		
		@Schema(description = "Short one-line description")
		String shortDescription,
		
		@Schema(description = "Credit hours")
		Integer creditHours,
		
		@Schema(description = "Course objectives")
		String courseObjectives,
		
		@Schema(description = "Prerequisites")
		String prerequisites,
		
		@Schema(description = "Recommended textbook")
		String textbook,
		
		@Schema(description = "Language of instruction")
		String language,
		
		@Schema(description = "Department")
		String department,
		
		@Schema(description = "Faculty")
		String faculty,
		
		@Schema(description = "Course level")
		String courseLevel,
		
		@Schema(description = "Semester")
		String semester,
		
		@Schema(description = "Academic year")
		String academicYear,
		
		@Schema(description = "Start date")
		LocalDate startDate,
		
		@Schema(description = "End date")
		LocalDate endDate,
		
		@Schema(description = "Duration in weeks")
		Integer durationWeeks,
		
		@Schema(description = "Grade level")
		GradeLevelStatus gradeLevel,
		
		@Schema(description = "Exam type")
		ExamType examType,
		
		@Schema(description = "Instructor name")
		String instructorName,
		
		@Schema(description = "Instructor email")
		String instructorEmail,
		
		@Schema(description = "Instructor phone")
		String instructorPhone,
		
		@Schema(description = "Co-instructor name")
		String coInstructorName,
		
		@Schema(description = "Room number")
		String roomNumber,
		
		@Schema(description = "Building")
		String building,
		
		@Schema(description = "Schedule day")
		ScheduleDayType scheduleDay,
		
		@Schema(description = "Schedule start time")
		LocalTime scheduleStartTime,
		
		@Schema(description = "Schedule end time")
		LocalTime scheduleEndTime,
		
		@Schema(description = "Schedule type")
		ScheduleType scheduleType,
		
		@Schema(description = "Online meeting link")
		String meetingLink,
		
		// ─── Status & Settings ────────────────────────────────────────────────
		@Schema(description = "Is course active")
		boolean isActive,
		
		@Schema(description = "Is mandatory")
		boolean isMandatory,
		
		@Schema(description = "Is elective")
		boolean isElective,
		
		@Schema(description = "Allow late enrollment")
		boolean allowLateEnrollment,
		
		@Schema(description = "Maximum capacity")
		Integer maxCapacity,
		
		@Schema(description = "Current enrollment count")
		Integer currentEnrollmentCount,
		
		@Schema(description = "Minimum enrollment count")
		Integer minEnrollmentCount,
		
		@Schema(description = "Pass mark")
		Double passMark,
		
		@Schema(description = "Maximum score")
		Double maxScore,
		
		@Schema(description = "Course status")
		CourseStatus courseStatus,
		
		@Schema(description = "Notes")
		String notes,
		
		@Schema(description = "Record creation timestamp")
		Instant createdAt,
		
		@Schema(description = "Record last updated timestamp")
		Instant updatedAt

) {}