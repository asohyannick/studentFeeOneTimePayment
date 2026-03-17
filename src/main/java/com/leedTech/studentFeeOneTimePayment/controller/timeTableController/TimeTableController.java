package com.leedTech.studentFeeOneTimePayment.controller.timeTableController;
import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.dto.timetable.TimetableRequestDTO;
import com.leedTech.studentFeeOneTimePayment.dto.timetable.TimetableResponseDTO;
import com.leedTech.studentFeeOneTimePayment.service.timeTableService.TimeTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/${api.version}/time-table")
@RequiredArgsConstructor
@Tag(name = "Timetable Management Endpoints", description = "Endpoints for managing school timetables")
public class TimeTableController {

			private final TimeTableService timeTableService;
			
			@Operation(
					summary = "Create a new timetable entry",
					description = "Creates a new timetable entry and returns the saved record"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "201", description = "Timetable entry created successfully"),
					@ApiResponse(responseCode = "400", description = "Invalid request body"),
					@ApiResponse(responseCode = "404", description = "Course or teacher not found")
			})
			@PostMapping("/add-timetable")
			public ResponseEntity<CustomResponseMessage<TimetableResponseDTO>> createTimeTable(
					@Valid @RequestBody TimetableRequestDTO dto
			) {
				TimetableResponseDTO data = timeTableService.createTimeTable(dto);
				return ResponseEntity
						       .status(HttpStatus.CREATED)
						       .body(new CustomResponseMessage<>(
								       "Timetable entry created successfully",
								       HttpStatus.CREATED.value(),
								       data
						       ));
			}
			
			@Operation(
					summary = "Fetch all timetable entries",
					description = "Returns a list of all timetable entries"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Timetable entries fetched successfully")
			})
			@GetMapping("/fetch-timetables")
			public ResponseEntity<CustomResponseMessage<List<TimetableResponseDTO>>> getAllTimeTables() {
				List<TimetableResponseDTO> data = timeTableService.getAllTimeTables();
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Timetable entries fetched successfully",
						HttpStatus.OK.value(),
						data
				));
			}
			
			@Operation(
					summary = "Fetch a single timetable entry",
					description = "Returns a single timetable entry by its ID"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Timetable entry fetched successfully"),
					@ApiResponse(responseCode = "404", description = "Timetable entry not found")
			})
			@GetMapping("/fetch-timetable/{id}")
			public ResponseEntity<CustomResponseMessage<TimetableResponseDTO>> getTimeTableById(
					@PathVariable UUID id
			) {
				TimetableResponseDTO data = timeTableService.getTimeTableById(id);
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Timetable entry fetched successfully",
						HttpStatus.OK.value(),
						data
				));
			}
			
			
			@Operation(
					summary = "Update a timetable entry",
					description = "Updates an existing timetable entry by its ID and returns the updated record"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Timetable entry updated successfully"),
					@ApiResponse(responseCode = "400", description = "Invalid request body"),
					@ApiResponse(responseCode = "404", description = "Timetable entry, course or teacher not found")
			})
			@PutMapping("/update-timetable/{id}")
			public ResponseEntity<CustomResponseMessage<TimetableResponseDTO>> updateTimeTable(
					@PathVariable UUID id,
					@Valid @RequestBody TimetableRequestDTO dto
			) {
				TimetableResponseDTO data = timeTableService.updateTimeTable(id, dto);
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Timetable entry updated successfully",
						HttpStatus.OK.value(),
						data
				));
			}
			
			@Operation(
					summary = "Delete a timetable entry",
					description = "Deletes a timetable entry by its ID"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Timetable entry deleted successfully"),
					@ApiResponse(responseCode = "404", description = "Timetable entry not found")
			})
			@DeleteMapping("/delete-timetable/{id}")
			public ResponseEntity<CustomResponseMessage<Void>> deleteTimeTable(
					@PathVariable UUID id
			) {
				timeTableService.deleteTimeTable(id);
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Timetable entry deleted successfully",
						HttpStatus.OK.value(),
						null
				));
			}
			
			@Operation(
					summary = "Count all timetable entries",
					description = "Returns the total number of timetable entries"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Timetable count fetched successfully")
			})
			@GetMapping("/count")
			public ResponseEntity<CustomResponseMessage<Long>> countTimeTables() {
				long count = timeTableService.countTimeTables();
				return ResponseEntity.ok(new CustomResponseMessage<>(
						"Timetable count fetched successfully",
						HttpStatus.OK.value(),
						count
				));
			}
			
			@Operation(
					summary = "Download timetable as PDF",
					description = "Generates and downloads the full timetable as a PDF file"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Timetable PDF generated successfully"),
					@ApiResponse(responseCode = "500", description = "Failed to generate PDF")
			})
			@GetMapping("/download-pdf")
			public ResponseEntity<byte[]> downloadTimetablePdf() {
				byte[] pdf = timeTableService.downloadTimetableAsPdf();
				return ResponseEntity
						       .status(HttpStatus.OK)
						       .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=timetable.pdf")
						       .contentType(MediaType.APPLICATION_PDF)
						       .body(pdf);
			}
}