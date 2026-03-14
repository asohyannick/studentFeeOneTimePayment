package com.leedTech.studentFeeOneTimePayment.controller.studentController;
import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus;
import com.leedTech.studentFeeOneTimePayment.constant.Gender;
import com.leedTech.studentFeeOneTimePayment.dto.studentProfile.StudentProfileRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.studentProfile.StudentProfileResponseDto;
import com.leedTech.studentFeeOneTimePayment.service.studentService.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/${api.version}/students")
@RequiredArgsConstructor
@Tag(name = "Student Management API Endpoints", description = "Endpoints for managing student profiles including creation, retrieval, update, deletion, search and filtering")
public class StudentController {

private final StudentService studentService;

@Operation(
		summary = "Create student profile",
		description = "Creates a new student profile with an optional profile picture upload via Cloudinary"
)
@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Student profile created successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input or duplicate data"),
		@ApiResponse(responseCode = "404", description = "User not found"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden")
})
@PostMapping(value = "/create-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<CustomResponseMessage<StudentProfileResponseDto>> createStudentProfile(
		@Valid @ModelAttribute StudentProfileRequestDto request
) {
	StudentProfileResponseDto data = studentService.createStudentProfile(request);
	return ResponseEntity.status(HttpStatus.CREATED).body(
			new CustomResponseMessage<>(
					"Student profile created successfully.",
					HttpStatus.CREATED.value(),
					data
			)
	);
}

@Operation(
		summary = "Fetch all student profiles",
		description = "Returns a paginated list of all non-deleted student profiles with sorting support"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Students fetched successfully"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden")
})
@GetMapping("/fetch-student-profiles")
public ResponseEntity<CustomResponseMessage<Page<StudentProfileResponseDto>>> fetchAllStudents(
		@Parameter(description = "Page number (0-based)", example = "0")
		@RequestParam(defaultValue = "0") int page,
		
		@Parameter(description = "Number of records per page", example = "10")
		@RequestParam(defaultValue = "10") int size,
		
		@Parameter(description = "Field to sort by", example = "createdAt")
		@RequestParam(defaultValue = "createdAt") String sortBy,
		
		@Parameter(description = "Sort direction: asc or desc", example = "desc")
		@RequestParam(defaultValue = "desc") String sortDir
) {
	Page<StudentProfileResponseDto> data = studentService.fetchAllStudents(page, size, sortBy, sortDir);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					data.isEmpty()
							? "No student profiles found."
							: data.getTotalElements() + " student profile(s) retrieved successfully.",
					HttpStatus.OK.value(),
					data
			)
	);
}

@Operation(
		summary = "Update student profile",
		description = "Updates an existing student profile. Optionally uploads a new profile picture to Cloudinary"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Student profile updated successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input or duplicate data"),
		@ApiResponse(responseCode = "404", description = "Student profile not found"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden")
})
@PutMapping(value = "/update-student-p/{studentProfileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<CustomResponseMessage<StudentProfileResponseDto>> updateStudentProfile(
		@PathVariable UUID studentProfileId,
		@Valid @ModelAttribute StudentProfileRequestDto request
) {
	StudentProfileResponseDto data = studentService.updateStudentProfile(
			studentProfileId, request
	);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Student profile updated successfully.",
					HttpStatus.OK.value(),
					data
			)
	);
}

@Operation(
		summary = "Delete student profile",
		description = "Soft deletes a student profile and removes their profile picture from Cloudinary"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Student profile deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Student profile not found"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@DeleteMapping("/delete-student-p/{studentProfileId}")
public ResponseEntity<CustomResponseMessage<Void>> deleteStudentProfile(
		@PathVariable UUID studentProfileId
) {
	studentService.deleteStudentProfile(studentProfileId);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Student profile deleted successfully.",
					HttpStatus.OK.value(),
					null
			)
	);
}

@Operation(
		summary = "Count all active students",
		description = "Returns the total count of active student profiles"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Student count retrieved successfully"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@GetMapping("/count-student-profiles")
public ResponseEntity<CustomResponseMessage<Long>> countAllStudents() {
	long count = studentService.countAllStudents();
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Total active students retrieved successfully.",
					HttpStatus.OK.value(),
					count
			)
	);
}

@Operation(
		summary = "Search and filter student profiles",
		description = "Searches and filters student profiles by any combination of properties with pagination and sorting support"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
		@ApiResponse(responseCode = "401", description = "Unauthorized")
})
@GetMapping("/search")
public ResponseEntity<CustomResponseMessage<Page<StudentProfileResponseDto>>> searchAndFilterStudents(
		@Parameter(description = "Keyword to search by name, email, student number or national ID")
		@RequestParam(required = false) String keyword,
		
		@Parameter(description = "Filter by current class", example = "Form 5")
		@RequestParam(required = false) String currentClass,
		
		@Parameter(description = "Filter by academic year", example = "2025-2026")
		@RequestParam(required = false) String academicYear,
		
		@Parameter(description = "Filter by current section", example = "A")
		@RequestParam(required = false) String currentSection,
		
		@Parameter(description = "Filter by enrollment status", example = "ACTIVE")
		@RequestParam(required = false) EnrollmentStatus enrollmentStatus,
		
		@Parameter(description = "Filter by gender", example = "MALE")
		@RequestParam(required = false) Gender gender,
		
		@Parameter(description = "Filter by nationality", example = "Cameroonian")
		@RequestParam(required = false) String nationality,
		
		@Parameter(description = "Filter by city", example = "Yaounde")
		@RequestParam(required = false) String city,
		
		@Parameter(description = "Filter by country", example = "Cameroon")
		@RequestParam(required = false) String country,
		
		@Parameter(description = "Filter fee defaulters", example = "false")
		@RequestParam(required = false) Boolean isFeeDefaulter,
		
		@Parameter(description = "Filter boarders", example = "false")
		@RequestParam(required = false) Boolean isBoarder,
		
		@Parameter(description = "Filter by school transport usage", example = "true")
		@RequestParam(required = false) Boolean usesSchoolTransport,
		
		@Parameter(description = "Filter by hostel name", example = "Block A Hostel")
		@RequestParam(required = false) String hostelName,
		
		@Parameter(description = "Filter by transport route", example = "Route 5 - Bastos")
		@RequestParam(required = false) String transportRoute,
		
		@Parameter(description = "Filter by minimum scholarship percentage", example = "50.0")
		@RequestParam(required = false) Double minScholarshipPercentage,
		
		@Parameter(description = "Page number (0-based)", example = "0")
		@RequestParam(defaultValue = "0") int page,
		
		@Parameter(description = "Number of records per page", example = "10")
		@RequestParam(defaultValue = "10") int size,
		
		@Parameter(description = "Field to sort by", example = "createdAt")
		@RequestParam(defaultValue = "createdAt") String sortBy,
		
		@Parameter(description = "Sort direction: asc or desc", example = "desc")
		@RequestParam(defaultValue = "desc") String sortDir
) {
	Page<StudentProfileResponseDto> data = studentService.searchAndFilterStudents(
			keyword, currentClass, academicYear, currentSection,
			enrollmentStatus, gender, nationality, city, country,
			isFeeDefaulter, isBoarder, usesSchoolTransport,
			hostelName, transportRoute, minScholarshipPercentage,
			page, size, sortBy, sortDir
	);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					data.isEmpty()
							? "No student profiles matched your search."
							: data.getTotalElements() + " student profile(s) found.",
					HttpStatus.OK.value(),
					data
			)
	);
}

@Operation(
		summary = "Fetch fee defaulters",
		description = "Returns all students who are fee defaulters for a given academic year"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Fee defaulters retrieved successfully"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@GetMapping("/fee-defaulters")
public ResponseEntity<CustomResponseMessage<List<StudentProfileResponseDto>>> fetchFeeDefaulters(
		@RequestParam String academicYear
) {
	List<StudentProfileResponseDto> data = studentService.fetchFeeDefaulters(academicYear);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					data.isEmpty()
							? "No fee defaulters found for academic year " + academicYear
							: data.size() + " fee defaulter(s) found for academic year " + academicYear,
					HttpStatus.OK.value(),
					data
			)
	);
}

@Operation(
		summary = "Fetch boarders by hostel",
		description = "Returns all student boarders assigned to a specific hostel"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Boarders retrieved successfully"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@GetMapping("/boarders")
public ResponseEntity<CustomResponseMessage<List<StudentProfileResponseDto>>> fetchBoardersByHostel(
		@RequestParam String hostelName
) {
	List<StudentProfileResponseDto> data = studentService.fetchBoardersByHostel(hostelName);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					data.isEmpty()
							? "No boarders found in hostel: " + hostelName
							: data.size() + " boarder(s) found in hostel: " + hostelName,
					HttpStatus.OK.value(),
					data
			)
	);
}

@Operation(
		summary = "Fetch students with scholarship",
		description = "Returns all students who have a scholarship equal to or above the specified minimum percentage"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Scholarship students retrieved successfully"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@GetMapping("/scholarships")
public ResponseEntity<CustomResponseMessage<List<StudentProfileResponseDto>>> fetchStudentsWithScholarship(
		@Parameter(description = "Minimum scholarship percentage", example = "50.0")
		@RequestParam(defaultValue = "0.0") Double minPercentage
) {
	List<StudentProfileResponseDto> data = studentService.fetchStudentsWithScholarship(minPercentage);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					data.isEmpty()
							? "No students found with scholarship above " + minPercentage + "%"
							: data.size() + " student(s) found with scholarship above " + minPercentage + "%",
					HttpStatus.OK.value(),
					data
			)
	);
}
}