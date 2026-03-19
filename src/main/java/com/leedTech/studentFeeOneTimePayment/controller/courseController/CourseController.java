package com.leedTech.studentFeeOneTimePayment.controller.courseController;
import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import com.leedTech.studentFeeOneTimePayment.dto.course.CourseRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.course.CourseResponseDto;
import com.leedTech.studentFeeOneTimePayment.service.courseService.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.UUID;
@RestController
@RequestMapping("/api/${api.version}/courses")
@RequiredArgsConstructor
@Tag(
		name = "Courses Management API Endpoints",
		description = "Endpoints responsible for managing courses including creation, retrieval, update, deletion, search and filtering"
)
public class CourseController {

		private final CourseService courseService;
		
		@Operation(
				summary = "Create a new course",
				description = "Creates a new course. Optionally uploads a course banner image to Cloudinary or accepts a URL"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "201", description = "Course created successfully"),
				@ApiResponse(responseCode = "400", description = "Invalid input or duplicate course code/name"),
				@ApiResponse(responseCode = "404", description = "Teacher not found"),
				@ApiResponse(responseCode = "401", description = "Unauthorized"),
				@ApiResponse(responseCode = "403", description = "Forbidden")
		})
		@PostMapping(value = "/create-course", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		public ResponseEntity<CustomResponseMessage<CourseResponseDto>> createCourse(
				@Valid @ModelAttribute CourseRequestDto request
		) {
			CourseResponseDto data = courseService.createCourse(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(
					new CustomResponseMessage<>(
							"Course created successfully.",
							HttpStatus.CREATED.value(),
							data
					)
			);
		}
		
		@Operation(
				summary = "Fetch all courses",
				description = "Returns a paginated list of all non-deleted courses with sorting support"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Courses fetched successfully"),
				@ApiResponse(responseCode = "401", description = "Unauthorized"),
				@ApiResponse(responseCode = "403", description = "Forbidden")
		})
		@GetMapping("/fetch-all-courses")
		public ResponseEntity<CustomResponseMessage<Page<CourseResponseDto>>> fetchAllCourses(
				@Parameter(description = "Page number (0-based)", example = "0")
				@RequestParam(defaultValue = "0") int page,
				
				@Parameter(description = "Number of records per page", example = "10")
				@RequestParam(defaultValue = "10") int size,
				
				@Parameter(description = "Field to sort by", example = "createdAt")
				@RequestParam(defaultValue = "createdAt") String sortBy,
				
				@Parameter(description = "Sort direction: asc or desc", example = "desc")
				@RequestParam(defaultValue = "desc") String sortDir
		) {
			Page<CourseResponseDto> data = courseService.fetchAllCourses(page, size, sortBy, sortDir);
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							data.isEmpty()
									? "No courses found."
									: data.getTotalElements() + " course(s) retrieved successfully.",
							HttpStatus.OK.value(),
							data
					)
			);
		}
		
		@Operation(
				summary = "Fetch course by ID",
				description = "Returns the full details of a single course by its UUID"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Course fetched successfully"),
				@ApiResponse(responseCode = "404", description = "Course not found"),
				@ApiResponse(responseCode = "401", description = "Unauthorized")
		})
		@GetMapping("/fetch-course/{courseId}")
		public ResponseEntity<CustomResponseMessage<CourseResponseDto>> fetchCourseById(
				@PathVariable UUID courseId
		) {
			CourseResponseDto data = courseService.fetchCourseById(courseId);
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							"Course retrieved successfully.",
							HttpStatus.OK.value(),
							data
					)
			);
		}
		
		@Operation(
				summary = "Fetch course by course code",
				description = "Returns the full details of a course by its unique course code"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Course fetched successfully"),
				@ApiResponse(responseCode = "404", description = "Course not found"),
				@ApiResponse(responseCode = "401", description = "Unauthorized")
		})
		@GetMapping("/code/{courseCode}")
		public ResponseEntity<CustomResponseMessage<CourseResponseDto>> fetchCourseByCourseCode(
				@PathVariable String courseCode
		) {
			CourseResponseDto data = courseService.fetchCourseByCourseCode(courseCode);
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							"Course retrieved successfully.",
							HttpStatus.OK.value(),
							data
					)
			);
		}
		
		@Operation(
				summary = "Update a course",
				description = "Updates an existing course. Optionally uploads a new banner image to Cloudinary"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Course updated successfully"),
				@ApiResponse(responseCode = "400", description = "Invalid input or duplicate course code/name"),
				@ApiResponse(responseCode = "404", description = "Course or teacher not found"),
				@ApiResponse(responseCode = "401", description = "Unauthorized"),
				@ApiResponse(responseCode = "403", description = "Forbidden")
		})
		@PutMapping(value = "/update-course/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		public ResponseEntity<CustomResponseMessage<CourseResponseDto>> updateCourse(
				@PathVariable UUID courseId,
				@Valid @ModelAttribute CourseRequestDto request
		) {
			CourseResponseDto data = courseService.updateCourse(courseId, request);
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							"Course updated successfully.",
							HttpStatus.OK.value(),
							data
					)
			);
		}
		
		@Operation(
				summary = "Delete a course",
				description = "Soft deletes a course and removes its banner image from Cloudinary"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Course deleted successfully"),
				@ApiResponse(responseCode = "404", description = "Course not found"),
				@ApiResponse(responseCode = "401", description = "Unauthorized"),
				@ApiResponse(responseCode = "403", description = "Forbidden — Admin access required")
		})
		@DeleteMapping("/delete-course/{courseId}")
		public ResponseEntity<CustomResponseMessage<Void>> deleteCourse(
				@PathVariable UUID courseId
		) {
			courseService.deleteCourse(courseId);
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							"Course deleted successfully.",
							HttpStatus.OK.value(),
							null
					)
			);
		}
		
		@Operation(
				summary = "Count all courses",
				description = "Returns the total count of all non-deleted courses"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
				@ApiResponse(responseCode = "401", description = "Unauthorized"),
				@ApiResponse(responseCode = "403", description = "Forbidden")
		})
		@GetMapping("/count-courses")
		public ResponseEntity<CustomResponseMessage<Long>> countAllCourses() {
			long count = courseService.countAllCourses();
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							"Total courses retrieved successfully.",
							HttpStatus.OK.value(),
							count
					)
			);
		}
		
		@Operation(
				summary = "Count active courses",
				description = "Returns the total count of currently active non-deleted courses"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
				@ApiResponse(responseCode = "401", description = "Unauthorized")
		})
		@GetMapping("/count/active")
		public ResponseEntity<CustomResponseMessage<Long>> countActiveCourses() {
			long count = courseService.countActiveCourses();
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							"Total active courses retrieved successfully.",
							HttpStatus.OK.value(),
							count
					)
			);
		}
		
		@Operation(
				summary = "Count courses by department",
				description = "Returns the total count of courses in a specific department"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
				@ApiResponse(responseCode = "401", description = "Unauthorized")
		})
		@GetMapping("/count/department")
		public ResponseEntity<CustomResponseMessage<Long>> countCoursesByDepartment(
				@RequestParam String department
		) {
			long count = courseService.countCoursesByDepartment(department);
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							"Total courses for " + department + " retrieved successfully.",
							HttpStatus.OK.value(),
							count
					)
			);
		}
		
		@Operation(
				summary = "Count courses by status",
				description = "Returns the total count of courses matching a specific status"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
				@ApiResponse(responseCode = "401", description = "Unauthorized")
		})
		@GetMapping("/count/status")
		public ResponseEntity<CustomResponseMessage<Long>> countCoursesByStatus(
				@RequestParam CourseStatus courseStatus
		) {
			long count = courseService.countCoursesByStatus(courseStatus);
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							"Total " + courseStatus + " courses retrieved successfully.",
							HttpStatus.OK.value(),
							count
					)
			);
		}
		
		@Operation(
				summary = "Search and filter courses",
				description = "Searches and filters courses by any combination of properties with pagination and sorting support"
		)
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
				@ApiResponse(responseCode = "401", description = "Unauthorized")
		})
		@GetMapping("/search")
		public ResponseEntity<CustomResponseMessage<Page<CourseResponseDto>>> searchAndFilterCourses(
				
				@Parameter(description = "Keyword to search by name, code, department, instructor or description")
				@RequestParam(required = false) String keyword,
				
				@Parameter(description = "Filter by department", example = "Mathematics")
				@RequestParam(required = false) String department,
				
				@Parameter(description = "Filter by faculty", example = "Science & Technology")
				@RequestParam(required = false) String faculty,
				
				@Parameter(description = "Filter by academic year", example = "2025-2026")
				@RequestParam(required = false) String academicYear,
				
				@Parameter(description = "Filter by semester", example = "First Semester")
				@RequestParam(required = false) String semester,
				
				@Parameter(description = "Filter by course level", example = "Form 5")
				@RequestParam(required = false) String courseLevel,
				
				@Parameter(description = "Filter by course status", example = "ONGOING")
				@RequestParam(required = false) CourseStatus courseStatus,
				
				@Parameter(description = "Filter by grade level", example = "INTERMEDIATE")
				@RequestParam(required = false) GradeLevelStatus gradeLevel,
				
				@Parameter(description = "Filter by exam type", example = "WRITTEN")
				@RequestParam(required = false) ExamType examType,
				
				@Parameter(description = "Filter by schedule type", example = "IN_PERSON")
				@RequestParam(required = false) ScheduleType scheduleType,
				
				@Parameter(description = "Filter by schedule day", example = "MONDAY")
				@RequestParam(required = false) ScheduleDayType scheduleDay,
				
				@Parameter(description = "Filter by instructor name")
				@RequestParam(required = false) String instructorName,
				
				@Parameter(description = "Filter by active status", example = "true")
				@RequestParam(required = false) Boolean isActive,
				
				@Parameter(description = "Filter mandatory courses", example = "false")
				@RequestParam(required = false) Boolean isMandatory,
				
				@Parameter(description = "Filter elective courses", example = "true")
				@RequestParam(required = false) Boolean isElective,
				
				@Parameter(description = "Filter by late enrollment allowed", example = "false")
				@RequestParam(required = false) Boolean allowLateEnrollment,
				
				@Parameter(description = "Filter by start date from (inclusive)", example = "2025-09-01")
				@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFrom,
				
				@Parameter(description = "Filter by start date to (inclusive)", example = "2026-06-30")
				@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateTo,
				
				@Parameter(description = "Minimum credit hours", example = "1")
				@RequestParam(required = false) Integer minCreditHours,
				
				@Parameter(description = "Maximum credit hours", example = "6")
				@RequestParam(required = false) Integer maxCreditHours,
				
				@Parameter(description = "Minimum pass mark", example = "40.0")
				@RequestParam(required = false) Double minPassMark,
				
				@Parameter(description = "Maximum pass mark", example = "60.0")
				@RequestParam(required = false) Double maxPassMark,
				
				@Parameter(description = "Minimum capacity", example = "10")
				@RequestParam(required = false) Integer minCapacity,
				
				@Parameter(description = "Maximum capacity", example = "100")
				@RequestParam(required = false) Integer maxCapacity,
				
				@Parameter(description = "Page number (0-based)", example = "0")
				@RequestParam(defaultValue = "0") int page,
				
				@Parameter(description = "Number of records per page", example = "10")
				@RequestParam(defaultValue = "10") int size,
				
				@Parameter(description = "Field to sort by", example = "createdAt")
				@RequestParam(defaultValue = "createdAt") String sortBy,
				
				@Parameter(description = "Sort direction: asc or desc", example = "desc")
				@RequestParam(defaultValue = "desc") String sortDir
		) {
			Page<CourseResponseDto> data = courseService.searchAndFilterCourses(
					keyword, department, faculty, academicYear, semester, courseLevel,
					courseStatus, gradeLevel, examType, scheduleType, scheduleDay,
					instructorName, isActive, isMandatory, isElective, allowLateEnrollment,
					startDateFrom, startDateTo, minCreditHours, maxCreditHours,
					minPassMark, maxPassMark, minCapacity, maxCapacity,
					page, size, sortBy, sortDir
			);
			return ResponseEntity.ok(
					new CustomResponseMessage<>(
							data.isEmpty()
									? "No courses matched your search."
									: data.getTotalElements() + " course(s) found.",
							HttpStatus.OK.value(),
							data
					)
			);
		}
}