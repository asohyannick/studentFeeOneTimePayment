package com.leedTech.studentFeeOneTimePayment.dto.course;

import com.leedTech.studentFeeOneTimePayment.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CourseRequest", description = "Request payload for creating or updating a course")
public class CourseRequestDto {

		@Schema(description = "Course name", example = "Introduction to Mathematics")
		@NotBlank(message = "Course name is required")
		@Size(max = 255, message = "Course name must not exceed 255 characters")
		private String courseName;

		@Schema(description = "UUID of the teacher who owns this course", example = "a3f1c2d4-12ab-4e56-89cd-abcdef012345")
		@NotNull(message = "Teacher ID is required")
		private UUID teacherId;
		
		@Schema(description = "Unique course code", example = "MATH-101")
		@NotBlank(message = "Course code is required")
		@Size(max = 20, message = "Course code must not exceed 20 characters")
		private String courseCode;
		
		@Schema(description = "Course URL as a valid web link", example = "https://leedtech.com/courses/math-101")
		@Pattern(
				regexp = "^$|^(https?://).*",
				message = "Course URL must be a valid URL starting with http:// or https://"
		)
		private String courseUrl;
		
		@Schema(description = "Course banner or thumbnail image file — upload as multipart file")
		private MultipartFile courseImageFile;
		
		@Schema(description = "Full course description")
		private String description;
		
		@Schema(description = "Short one-line description", example = "An intro to basic maths concepts")
		@Size(max = 500, message = "Short description must not exceed 500 characters")
		private String shortDescription;
		
		@Schema(description = "Credit hours", example = "3")
		@NotNull(message = "Credit hours are required")
		@Min(value = 1, message = "Credit hours must be at least 1")
		@Max(value = 20, message = "Credit hours must not exceed 20")
		private Integer creditHours;
		
		@Schema(description = "Course learning objectives")
		private String courseObjectives;
		
		@Schema(description = "Prerequisite course codes", example = "MATH-100, ENG-100")
		@Size(max = 500, message = "Prerequisites must not exceed 500 characters")
		private String prerequisites;
		
		@Schema(description = "Recommended textbook", example = "Calculus by Stewart")
		@Size(max = 255, message = "Textbook must not exceed 255 characters")
		private String textbook;
		
		@Schema(description = "Language of instruction", example = "English")
		@Size(max = 50, message = "Language must not exceed 50 characters")
		private String language;
		
		@Schema(description = "Department offering the course", example = "Mathematics")
		@NotBlank(message = "Department is required")
		@Size(max = 100, message = "Department must not exceed 100 characters")
		private String department;
		
		@Schema(description = "Faculty offering the course", example = "Science & Technology")
		@Size(max = 100, message = "Faculty must not exceed 100 characters")
		private String faculty;
		
		@Schema(description = "Course level", example = "Form 5")
		@Size(max = 100, message = "Course level must not exceed 100 characters")
		private String courseLevel;
		
		@Schema(description = "Semester", example = "First Semester")
		@Size(max = 50, message = "Semester must not exceed 50 characters")
		private String semester;
		
		@Schema(description = "Academic year", example = "2025-2026")
		@NotBlank(message = "Academic year is required")
		@Size(max = 20, message = "Academic year must not exceed 20 characters")
		private String academicYear;
		
		@Schema(description = "Course start date", example = "2025-09-01")
		private LocalDate startDate;
		
		@Schema(description = "Course end date", example = "2026-06-30")
		private LocalDate endDate;
		
		@Schema(description = "Duration in weeks", example = "36")
		@Min(value = 1, message = "Duration must be at least 1 week")
		private Integer durationWeeks;
		
		@Schema(description = "Grade level", example = "INTERMEDIATE")
		private GradeLevelStatus gradeLevel;
		
		@Schema(description = "Exam type", example = "WRITTEN")
		private ExamType examType;
		
		@Schema(description = "Lead instructor name", example = "Dr. John Doe")
		@Size(max = 100, message = "Instructor name must not exceed 100 characters")
		private String instructorName;
		
		@Schema(description = "Instructor email", example = "instructor@leedtech.com")
		@Email(message = "Instructor email must be valid")
		private String instructorEmail;
		
		@Schema(description = "Instructor phone", example = "+237612345678")
		@Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Instructor phone must be valid")
		private String instructorPhone;
		
		@Schema(description = "Co-instructor name", example = "Mr. James Brown")
		@Size(max = 100, message = "Co-instructor name must not exceed 100 characters")
		private String coInstructorName;
		
		@Schema(description = "Room number", example = "B-204")
		@Size(max = 20, message = "Room number must not exceed 20 characters")
		private String roomNumber;
		
		@Schema(description = "Building name", example = "Block A")
		@Size(max = 100, message = "Building must not exceed 100 characters")
		private String building;
		
		@Schema(description = "Schedule day", example = "MONDAY")
		private ScheduleDayType scheduleDay;
		
		@Schema(description = "Schedule start time", example = "08:00")
		private LocalTime scheduleStartTime;
		
		@Schema(description = "Schedule end time", example = "10:00")
		private LocalTime scheduleEndTime;
		
		@Schema(description = "Schedule type", example = "IN_PERSON")
		private ScheduleType scheduleType;
		
		@Schema(description = "Online meeting link", example = "https://meet.google.com/abc-xyz")
		@Pattern(
				regexp = "^$|^(https?://).*",
				message = "Meeting link must be a valid URL"
		)
		private String meetingLink;
		
		@Schema(description = "Whether the course is active", example = "true")
		private boolean isActive = true;
		
		@Schema(description = "Whether the course is mandatory", example = "false")
		private boolean isMandatory;
		
		@Schema(description = "Whether the course is an elective", example = "true")
		private boolean isElective;
		
		@Schema(description = "Whether late enrollment is allowed", example = "false")
		private boolean allowLateEnrollment;
		
		@Schema(description = "Maximum student capacity", example = "40")
		@NotNull(message = "Max capacity is required")
		@Min(value = 1, message = "Max capacity must be at least 1")
		private Integer maxCapacity;
		
		@Schema(description = "Minimum students required to run the course", example = "10")
		@Min(value = 1, message = "Min enrollment must be at least 1")
		private Integer minEnrollmentCount;
		
		@Schema(description = "Pass mark percentage", example = "50.0")
		@NotNull(message = "Pass mark is required")
		@DecimalMin(value = "0.0", message = "Pass mark must be at least 0")
		@DecimalMax(value = "100.0", message = "Pass mark must not exceed 100")
		private Double passMark;
		
		@Schema(description = "Maximum achievable score", example = "100.0")
		@DecimalMin(value = "0.0", message = "Max score must be at least 0")
		private Double maxScore;
		
		@Schema(description = "Course status", example = "UPCOMING")
		private CourseStatus courseStatus;
		
		@Schema(description = "Additional notes")
		@Size(max = 1000, message = "Notes must not exceed 1000 characters")
		private String notes;
}