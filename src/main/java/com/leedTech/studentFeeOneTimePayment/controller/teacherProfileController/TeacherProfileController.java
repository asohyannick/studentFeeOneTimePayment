package com.leedTech.studentFeeOneTimePayment.controller.teacherProfileController;
import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import com.leedTech.studentFeeOneTimePayment.dto.teacherProfile.TeacherProfileRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.teacherProfile.TeacherProfileResponseDto;
import com.leedTech.studentFeeOneTimePayment.service.teacherService.TeacherProfileService;
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
@RequestMapping("/api/${api.version}/teacher-profiles")
@RequiredArgsConstructor
@Tag(
		name = "Teacher Profile Management API Endpoints",
		description = "Endpoints for managing teacher profiles including creation, retrieval, update, deletion, search and filtering"
)
public class TeacherProfileController {

			private final TeacherProfileService teacherProfileService;
			
			@Operation(
					summary = "Create teacher profile",
					description = "Creates a new teacher profile with an optional profile picture upload via Cloudinary or a URL"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "201", description = "Teacher profile created successfully"),
					@ApiResponse(responseCode = "400", description = "Invalid input or duplicate data"),
					@ApiResponse(responseCode = "404", description = "Teacher user not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden")
			})
			@PostMapping(value = "/create-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
			public ResponseEntity<CustomResponseMessage<TeacherProfileResponseDto>> createTeacherProfile(
					@Valid @ModelAttribute TeacherProfileRequestDto request
			) {
				TeacherProfileResponseDto data = teacherProfileService.createTeacherProfile(request);
				return ResponseEntity.status(HttpStatus.CREATED).body(
						new CustomResponseMessage<>(
								"Teacher profile created successfully.",
								HttpStatus.CREATED.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Fetch all teacher profiles",
					description = "Returns a paginated list of all non-deleted teacher profiles with sorting support"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Teacher profiles fetched successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden")
			})
			@GetMapping("/fetch-all")
			public ResponseEntity<CustomResponseMessage<Page<TeacherProfileResponseDto>>> fetchAllTeacherProfiles(
					@Parameter(description = "Page number (0-based)", example = "0")
					@RequestParam(defaultValue = "0") int page,
					
					@Parameter(description = "Number of records per page", example = "10")
					@RequestParam(defaultValue = "10") int size,
					
					@Parameter(description = "Field to sort by", example = "createdAt")
					@RequestParam(defaultValue = "createdAt") String sortBy,
					
					@Parameter(description = "Sort direction: asc or desc", example = "desc")
					@RequestParam(defaultValue = "desc") String sortDir
			) {
				Page<TeacherProfileResponseDto> data = teacherProfileService
						                                       .fetchAllTeacherProfiles(page, size, sortBy, sortDir);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								data.isEmpty()
										? "No teacher profiles found."
										: data.getTotalElements() + " teacher profile(s) retrieved successfully.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Fetch teacher profile by ID",
					description = "Returns the full details of a single teacher profile by its UUID"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Teacher profile fetched successfully"),
					@ApiResponse(responseCode = "404", description = "Teacher profile not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/{profileId}")
			public ResponseEntity<CustomResponseMessage<TeacherProfileResponseDto>> fetchTeacherProfileById(
					@PathVariable UUID profileId
			) {
				TeacherProfileResponseDto data = teacherProfileService.fetchTeacherProfileById(profileId);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Teacher profile retrieved successfully.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Fetch teacher profile by teacher user ID",
					description = "Returns the full details of a teacher profile linked to a specific user account UUID"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Teacher profile fetched successfully"),
					@ApiResponse(responseCode = "404", description = "Teacher profile not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/teacher/{teacherId}")
			public ResponseEntity<CustomResponseMessage<TeacherProfileResponseDto>> fetchTeacherProfileByTeacherId(
					@PathVariable UUID teacherId
			) {
				TeacherProfileResponseDto data = teacherProfileService
						                                 .fetchTeacherProfileByTeacherId(teacherId);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Teacher profile retrieved successfully.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Fetch teacher profile by employee ID",
					description = "Returns the full details of a teacher profile by their unique employee ID"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Teacher profile fetched successfully"),
					@ApiResponse(responseCode = "404", description = "Teacher profile not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/employee/{employeeId}")
			public ResponseEntity<CustomResponseMessage<TeacherProfileResponseDto>> fetchTeacherProfileByEmployeeId(
					@PathVariable String employeeId
			) {
				TeacherProfileResponseDto data = teacherProfileService
						                                 .fetchTeacherProfileByEmployeeId(employeeId);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Teacher profile retrieved successfully.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Update teacher profile",
					description = "Updates an existing teacher profile. Optionally uploads a new profile picture to Cloudinary"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Teacher profile updated successfully"),
					@ApiResponse(responseCode = "400", description = "Invalid input or duplicate data"),
					@ApiResponse(responseCode = "404", description = "Teacher profile not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden")
			})
			@PutMapping(value = "/update/{profileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
			public ResponseEntity<CustomResponseMessage<TeacherProfileResponseDto>> updateTeacherProfile(
					@PathVariable UUID profileId,
					@Valid @ModelAttribute TeacherProfileRequestDto request
			) {
				TeacherProfileResponseDto data = teacherProfileService
						                                 .updateTeacherProfile(profileId, request);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Teacher profile updated successfully.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Delete teacher profile",
					description = "Soft deletes a teacher profile and removes their profile picture from Cloudinary"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Teacher profile deleted successfully"),
					@ApiResponse(responseCode = "404", description = "Teacher profile not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden — Admin access required")
			})
			@DeleteMapping("/delete/{profileId}")
			public ResponseEntity<CustomResponseMessage<Void>> deleteTeacherProfile(
					@PathVariable UUID profileId
			) {
				teacherProfileService.deleteTeacherProfile(profileId);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Teacher profile deleted successfully.",
								HttpStatus.OK.value(),
								null
						)
				);
			}
			
			@Operation(
					summary = "Count all teacher profiles",
					description = "Returns the total count of all non-deleted teacher profiles"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden")
			})
			@GetMapping("/count")
			public ResponseEntity<CustomResponseMessage<Long>> countAllTeacherProfiles() {
				long count = teacherProfileService.countAllTeacherProfiles();
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total teacher profiles retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Count active teacher profiles",
					description = "Returns the total count of currently active non-deleted teacher profiles"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/count/active")
			public ResponseEntity<CustomResponseMessage<Long>> countActiveTeacherProfiles() {
				long count = teacherProfileService.countActiveTeacherProfiles();
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total active teacher profiles retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Count teacher profiles by department",
					description = "Returns the total count of teacher profiles in a specific department"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/count/department")
			public ResponseEntity<CustomResponseMessage<Long>> countByDepartment(
					@RequestParam String department
			) {
				long count = teacherProfileService.countByDepartment(department);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total teacher profiles for department '" + department + "' retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Count teacher profiles by employment status",
					description = "Returns the total count of teacher profiles matching a specific employment status"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/count/status")
			public ResponseEntity<CustomResponseMessage<Long>> countByEmploymentStatus(
					@RequestParam EmploymentStatus employmentStatus
			) {
				long count = teacherProfileService.countByEmploymentStatus(employmentStatus);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total " + employmentStatus + " teacher profiles retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Count teachers currently on leave",
					description = "Returns the total count of teachers currently on leave"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/count/on-leave")
			public ResponseEntity<CustomResponseMessage<Long>> countOnLeave() {
				long count = teacherProfileService.countOnLeave();
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total teachers on leave retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Count verified teacher profiles",
					description = "Returns the total count of teacher profiles with verified credentials"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/count/verified")
			public ResponseEntity<CustomResponseMessage<Long>> countVerified() {
				long count = teacherProfileService.countVerified();
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total verified teacher profiles retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Search and filter teacher profiles",
					description = "Searches and filters teacher profiles by any combination of properties with pagination and sorting support"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Teacher profiles retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/search")
			public ResponseEntity<CustomResponseMessage<Page<TeacherProfileResponseDto>>> searchAndFilterTeacherProfiles(
					
					@Parameter(description = "Keyword to search by name, email, department, specialization, skills or employee ID")
					@RequestParam(required = false) String keyword,
					
					@Parameter(description = "Filter by department", example = "Mathematics")
					@RequestParam(required = false) String department,
					
					@Parameter(description = "Filter by faculty", example = "Science & Technology")
					@RequestParam(required = false) String faculty,
					
					@Parameter(description = "Filter by academic year", example = "2025-2026")
					@RequestParam(required = false) String academicYear,
					
					@Parameter(description = "Filter by specialization", example = "Organic Chemistry")
					@RequestParam(required = false) String specialization,
					
					@Parameter(description = "Filter by highest qualification", example = "PhD")
					@RequestParam(required = false) String highestQualification,
					
					@Parameter(description = "Filter by subjects taught", example = "Mathematics")
					@RequestParam(required = false) String subjectsTaught,
					
					@Parameter(description = "Filter by teacher type", example = "FULL_TIME")
					@RequestParam(required = false) TeacherType teacherType,
					
					@Parameter(description = "Filter by employment type", example = "FULL_TIME")
					@RequestParam(required = false) EmploymentType employmentType,
					
					@Parameter(description = "Filter by employment status", example = "ACTIVE")
					@RequestParam(required = false) EmploymentStatus employmentStatus,
					
					@Parameter(description = "Filter by gender", example = "MALE")
					@RequestParam(required = false) Gender gender,
					
					@Parameter(description = "Filter by nationality", example = "Cameroonian")
					@RequestParam(required = false) String nationality,
					
					@Parameter(description = "Filter by city", example = "Yaounde")
					@RequestParam(required = false) String city,
					
					@Parameter(description = "Filter by country", example = "Cameroon")
					@RequestParam(required = false) String country,
					
					@Parameter(description = "Filter by active status", example = "true")
					@RequestParam(required = false) Boolean active,
					
					@Parameter(description = "Filter by verified status", example = "true")
					@RequestParam(required = false) Boolean verified,
					
					@Parameter(description = "Filter by blocked status", example = "false")
					@RequestParam(required = false) Boolean blocked,
					
					@Parameter(description = "Filter by on-leave status", example = "false")
					@RequestParam(required = false) Boolean onLeave,
					
					@Parameter(description = "Minimum years of experience", example = "2")
					@RequestParam(required = false) Integer minYearsOfExperience,
					
					@Parameter(description = "Maximum years of experience", example = "20")
					@RequestParam(required = false) Integer maxYearsOfExperience,
					
					@Parameter(description = "Minimum monthly salary", example = "100000.0")
					@RequestParam(required = false) Double minSalary,
					
					@Parameter(description = "Maximum monthly salary", example = "500000.0")
					@RequestParam(required = false) Double maxSalary,
					
					@Parameter(description = "Filter by join date from (inclusive)", example = "2018-01-01")
					@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate joinDateFrom,
					
					@Parameter(description = "Filter by join date to (inclusive)", example = "2024-12-31")
					@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate joinDateTo,
					
					@Parameter(description = "Page number (0-based)", example = "0")
					@RequestParam(defaultValue = "0") int page,
					
					@Parameter(description = "Number of records per page", example = "10")
					@RequestParam(defaultValue = "10") int size,
					
					@Parameter(description = "Field to sort by", example = "createdAt")
					@RequestParam(defaultValue = "createdAt") String sortBy,
					
					@Parameter(description = "Sort direction: asc or desc", example = "desc")
					@RequestParam(defaultValue = "desc") String sortDir
			) {
				Page<TeacherProfileResponseDto> data = teacherProfileService.searchAndFilterTeacherProfiles(
						keyword, department, faculty, academicYear, specialization,
						highestQualification, subjectsTaught, teacherType, employmentType,
						employmentStatus, gender, nationality, city, country,
						active, verified, blocked, onLeave,
						minYearsOfExperience, maxYearsOfExperience,
						minSalary, maxSalary, joinDateFrom, joinDateTo,
						page, size, sortBy, sortDir
				);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								data.isEmpty()
										? "No teacher profiles matched your search."
										: data.getTotalElements() + " teacher profile(s) found.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
}
