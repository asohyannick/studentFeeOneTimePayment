package com.leedTech.studentFeeOneTimePayment.controller.assignmentController;

import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.dto.assignment.AssignmentRequestDTO;
import com.leedTech.studentFeeOneTimePayment.dto.assignment.AssignmentResponseDTO;
import com.leedTech.studentFeeOneTimePayment.service.assignmentService.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/assignment")
@RequiredArgsConstructor
@Tag(name = "Assignment Management Endpoints", description = "Endpoints for managing assignments")
public class AssignmentController {

			private final AssignmentService assignmentService;
			
			@Operation(
					summary = "Create a new assignment",
					description = "Creates a new assignment and returns the saved record"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "201", description = "Assignment created successfully"),
					@ApiResponse(responseCode = "400", description = "Invalid request body"),
					@ApiResponse(responseCode = "404", description = "Teacher or student not found")
			})
			@PostMapping("/add-assignment")
			public ResponseEntity<CustomResponseMessage<AssignmentResponseDTO>> createAssignment(
					@Valid @RequestBody AssignmentRequestDTO dto
			) {
				AssignmentResponseDTO data = assignmentService.createAssignment(dto);
				return ResponseEntity
						       .status(HttpStatus.CREATED)
						       .body(new CustomResponseMessage<>(
								       "Assignment created successfully",
								       HttpStatus.CREATED.value(),
								       data
						       ));
			}
			
			@Operation(
					summary = "Fetch all assignments",
					description = "Returns a list of all assignments"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Assignments fetched successfully")
			})
			@GetMapping("/fetch-assignments")
			public ResponseEntity<CustomResponseMessage<List<AssignmentResponseDTO>>> getAllAssignments() {
				List<AssignmentResponseDTO> data = assignmentService.getAllAssignments();
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Assignments fetched successfully",
						HttpStatus.OK.value(),
						data
				));
			}
			
			@Operation(
					summary = "Fetch a single assignment",
					description = "Returns a single assignment by its ID"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Assignment fetched successfully"),
					@ApiResponse(responseCode = "404", description = "Assignment not found")
			})
			@GetMapping("/fetch-assignment/{id}")
			public ResponseEntity<CustomResponseMessage<AssignmentResponseDTO>> getAssignmentById(
					@PathVariable UUID id
			) {
				AssignmentResponseDTO data = assignmentService.getAssignmentById(id);
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Assignment fetched successfully",
						HttpStatus.OK.value(),
						data
				));
			}
			
			@Operation(
					summary = "Update an assignment",
					description = "Updates an existing assignment by its ID and returns the updated record"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Assignment updated successfully"),
					@ApiResponse(responseCode = "400", description = "Invalid request body"),
					@ApiResponse(responseCode = "404", description = "Assignment, teacher or student not found")
			})
			@PutMapping("/update-assignment/{id}")
			public ResponseEntity<CustomResponseMessage<AssignmentResponseDTO>> updateAssignment(
					@PathVariable UUID id,
					@Valid @RequestBody AssignmentRequestDTO dto
			) {
				AssignmentResponseDTO data = assignmentService.updateAssignment(id, dto);
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Assignment updated successfully",
						HttpStatus.OK.value(),
						data
				));
			}
			
			@Operation(
					summary = "Delete an assignment",
					description = "Deletes an assignment by its ID"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Assignment deleted successfully"),
					@ApiResponse(responseCode = "404", description = "Assignment not found")
			})
			@DeleteMapping("/delete-assignment/{id}")
			public ResponseEntity<CustomResponseMessage<Void>> deleteAssignment(
					@PathVariable UUID id
			) {
				assignmentService.deleteAssignment(id);
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Assignment deleted successfully",
						HttpStatus.OK.value(),
						null
				));
			}
			
			@Operation(
					summary = "Count all assignments",
					description = "Returns the total number of assignments"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Assignment count fetched successfully")
			})
			@GetMapping("/count")
			public ResponseEntity<CustomResponseMessage<Long>> countAssignments() {
				long count = assignmentService.countAssignments();
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Assignment count fetched successfully",
						HttpStatus.OK.value(),
						count
				));
			}
}