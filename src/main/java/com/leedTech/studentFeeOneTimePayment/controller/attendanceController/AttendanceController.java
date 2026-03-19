package com.leedTech.studentFeeOneTimePayment.controller.attendanceController;
import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.dto.attendance.AttendanceFilterDto;
import com.leedTech.studentFeeOneTimePayment.dto.attendance.AttendanceResponseDto;
import com.leedTech.studentFeeOneTimePayment.dto.attendance.CreateAttendanceRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.attendance.UpdateAttendanceRequestDto;
import com.leedTech.studentFeeOneTimePayment.entity.attendance.Attendance;
import com.leedTech.studentFeeOneTimePayment.service.attendanceService.AttendanceService;
import com.leedTech.studentFeeOneTimePayment.specification.AttendanceSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/${api.version}/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management Endpoints", description = "Endpoints for managing student attendance records")
public class AttendanceController {

	private final AttendanceService attendanceService;
	
	@PostMapping("/create-attendance")
	@Operation(summary = "Create a new attendance record")
	public ResponseEntity<CustomResponseMessage<AttendanceResponseDto>> createAttendance(
			@Valid @RequestBody CreateAttendanceRequestDto request) {
		
		AttendanceResponseDto data = attendanceService.createAttendance(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResponseMessage<>(
				"Attendance recorded successfully",
				HttpStatus.CREATED.value(),
				data
		));
	}
	
	@GetMapping("/fetch-attendances")
	@Operation(summary = "Fetch all attendance records with filtering, sorting and pagination")
	public ResponseEntity<CustomResponseMessage<Page<AttendanceResponseDto>>> getAllAttendances(
			
			@ParameterObject AttendanceFilterDto filter,
			@ParameterObject Pageable pageable
	) {
		
		Specification<Attendance> spec = AttendanceSpecification.build(filter);
		
		Page <AttendanceResponseDto> data =
				attendanceService.getAllAttendances(spec, pageable);
		
		return ResponseEntity.ok(
				new CustomResponseMessage<>(
						"Attendance records retrieved successfully",
						HttpStatus.OK.value(),
						data
				)
		);
	}
	
	@GetMapping("/fetch-attendance/{id}")
	@Operation(summary = "Get a single attendance record by ID")
	public ResponseEntity<CustomResponseMessage<AttendanceResponseDto>> getAttendanceById(@PathVariable UUID id) {
		
		AttendanceResponseDto data = attendanceService.getAttendanceById(id);
		return ResponseEntity.ok(new CustomResponseMessage<>(
				"Attendance record found",
				HttpStatus.OK.value(),
				data
		));
	}
	
	@PatchMapping("/update-attendance/{id}")
	@Operation(summary = "Update an existing attendance record")
	public ResponseEntity<CustomResponseMessage<AttendanceResponseDto>> updateAttendance(
			@Valid @RequestBody UpdateAttendanceRequestDto request) {
		
		AttendanceResponseDto data = attendanceService.updateAttendance(request);
		return ResponseEntity.ok(new CustomResponseMessage<>(
				"Attendance record updated successfully",
				HttpStatus.OK.value(),
				data
		));
	}
	
	@GetMapping("/count")
	@Operation(summary = "Count total attendance records based on filters")
	public ResponseEntity<CustomResponseMessage<Long>> countAttendances() {
		
		long count = attendanceService.countAllAttendances();
		return ResponseEntity.ok(new CustomResponseMessage<>(
				"Attendance count retrieved successfully",
				HttpStatus.OK.value(),
				count
		));
	}
	
	@DeleteMapping("/delete-attendance/{id}")
	@Operation(summary = "Soft delete an attendance record")
	public ResponseEntity<CustomResponseMessage<Void>> deleteAttendance(@PathVariable UUID id) {
		
		attendanceService.deleteAttendance(id);
		return ResponseEntity.ok(new CustomResponseMessage<>(
				"Attendance record deleted successfully",
				HttpStatus.OK.value(),
				null
		));
	}
}