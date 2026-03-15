package com.leedTech.studentFeeOneTimePayment.controller.reviewController;
import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.constant.ReviewStatus;
import com.leedTech.studentFeeOneTimePayment.dto.review.ReviewRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.review.ReviewResponseDto;
import com.leedTech.studentFeeOneTimePayment.service.reviewService.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/reviews")
@RequiredArgsConstructor
@Tag(
		name = "Review Management API Endpoints",
		description = "Endpoints for managing course reviews including creation, retrieval, update, deletion, search and filtering"
)
public class ReviewController {

			private final ReviewService reviewService;
			
			@Operation(
					summary = "Submit a course review",
					description = "Allows a student to submit a review for a course. One review per student per course is allowed"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "201", description = "Review submitted successfully"),
					@ApiResponse(responseCode = "400", description = "Invalid input or duplicate review"),
					@ApiResponse(responseCode = "404", description = "Course or student not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden")
			})
			@PostMapping("/submit")
			public ResponseEntity<CustomResponseMessage<ReviewResponseDto>> createReview(
					@Valid @RequestBody ReviewRequestDto request
			) {
				ReviewResponseDto data = reviewService.createReview(request);
				return ResponseEntity.status(HttpStatus.CREATED).body(
						new CustomResponseMessage<>(
								"Review submitted successfully. It is pending approval.",
								HttpStatus.CREATED.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Fetch all reviews",
					description = "Returns a paginated list of all non-deleted reviews with sorting support"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Reviews fetched successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden — Admin access required")
			})
			@GetMapping("/fetch-all")
			public ResponseEntity<CustomResponseMessage<Page<ReviewResponseDto>>> fetchAllReviews(
					@Parameter(description = "Page number (0-based)", example = "0")
					@RequestParam(defaultValue = "0") int page,
					
					@Parameter(description = "Number of records per page", example = "10")
					@RequestParam(defaultValue = "10") int size,
					
					@Parameter(description = "Field to sort by", example = "createdAt")
					@RequestParam(defaultValue = "createdAt") String sortBy,
					
					@Parameter(description = "Sort direction: asc or desc", example = "desc")
					@RequestParam(defaultValue = "desc") String sortDir
			) {
				Page<ReviewResponseDto> data = reviewService.fetchAllReviews(page, size, sortBy, sortDir);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								data.isEmpty()
										? "No reviews found."
										: data.getTotalElements() + " review(s) retrieved successfully.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Fetch reviews by course",
					description = "Returns a paginated list of approved reviews for a specific course"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Reviews fetched successfully"),
					@ApiResponse(responseCode = "404", description = "Course not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/course/{courseId}")
			public ResponseEntity<CustomResponseMessage<Page<ReviewResponseDto>>> fetchReviewsByCourse(
					@PathVariable UUID courseId,
					
					@Parameter(description = "Page number (0-based)", example = "0")
					@RequestParam(defaultValue = "0") int page,
					
					@Parameter(description = "Number of records per page", example = "10")
					@RequestParam(defaultValue = "10") int size,
					
					@Parameter(description = "Field to sort by", example = "createdAt")
					@RequestParam(defaultValue = "createdAt") String sortBy,
					
					@Parameter(description = "Sort direction: asc or desc", example = "desc")
					@RequestParam(defaultValue = "desc") String sortDir
			) {
				Page<ReviewResponseDto> data = reviewService.fetchReviewsByCourse(
						courseId, page, size, sortBy, sortDir
				);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								data.isEmpty()
										? "No reviews found for this course."
										: data.getTotalElements() + " review(s) found for this course.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Fetch reviews by student",
					description = "Returns a paginated list of all reviews submitted by a specific student"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Reviews fetched successfully"),
					@ApiResponse(responseCode = "404", description = "Student not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/student/{studentId}")
			public ResponseEntity<CustomResponseMessage<Page<ReviewResponseDto>>> fetchReviewsByStudent(
					@PathVariable UUID studentId,
					
					@Parameter(description = "Page number (0-based)", example = "0")
					@RequestParam(defaultValue = "0") int page,
					
					@Parameter(description = "Number of records per page", example = "10")
					@RequestParam(defaultValue = "10") int size,
					
					@Parameter(description = "Field to sort by", example = "createdAt")
					@RequestParam(defaultValue = "createdAt") String sortBy,
					
					@Parameter(description = "Sort direction: asc or desc", example = "desc")
					@RequestParam(defaultValue = "desc") String sortDir
			) {
				Page<ReviewResponseDto> data = reviewService.fetchReviewsByStudent(
						studentId, page, size, sortBy, sortDir
				);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								data.isEmpty()
										? "No reviews found for this student."
										: data.getTotalElements() + " review(s) found for this student.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Fetch review by ID",
					description = "Returns the full details of a single review by its UUID"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Review fetched successfully"),
					@ApiResponse(responseCode = "404", description = "Review not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/{reviewId}")
			public ResponseEntity<CustomResponseMessage<ReviewResponseDto>> fetchReviewById(
					@PathVariable UUID reviewId
			) {
				ReviewResponseDto data = reviewService.fetchReviewById(reviewId);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Review retrieved successfully.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Update a review",
					description = "Updates an existing review. Review is reset to PENDING status after editing and requires re-approval"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Review updated successfully"),
					@ApiResponse(responseCode = "400", description = "Invalid input"),
					@ApiResponse(responseCode = "404", description = "Review not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden")
			})
			@PutMapping("/update/{reviewId}")
			public ResponseEntity<CustomResponseMessage<ReviewResponseDto>> updateReview(
					@PathVariable UUID reviewId,
					@Valid @RequestBody ReviewRequestDto request
			) {
				ReviewResponseDto data = reviewService.updateReview(reviewId, request);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Review updated successfully. It is pending re-approval.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
			
			@Operation(
					summary = "Delete a review",
					description = "Soft deletes a review by its UUID"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Review deleted successfully"),
					@ApiResponse(responseCode = "404", description = "Review not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden — Admin or owner access required")
			})
			@DeleteMapping("/delete/{reviewId}")
			public ResponseEntity<CustomResponseMessage<Void>> deleteReview(
					@PathVariable UUID reviewId
			) {
				reviewService.deleteReview(reviewId);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Review deleted successfully.",
								HttpStatus.OK.value(),
								null
						)
				);
			}
			
			@Operation(
					summary = "Count all reviews",
					description = "Returns the total count of all non-deleted reviews"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden — Admin access required")
			})
			@GetMapping("/count")
			public ResponseEntity<CustomResponseMessage<Long>> countAllReviews() {
				long count = reviewService.countAllReviews();
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total reviews retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Count reviews by course",
					description = "Returns the total count of approved reviews for a specific course"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "404", description = "Course not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/count/course/{courseId}")
			public ResponseEntity<CustomResponseMessage<Long>> countReviewsByCourse(
					@PathVariable UUID courseId
			) {
				long count = reviewService.countReviewsByCourse(courseId);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total approved reviews for this course retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Count pending reviews",
					description = "Returns the total count of reviews awaiting moderation approval"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden — Admin access required")
			})
			@GetMapping("/count/pending")
			public ResponseEntity<CustomResponseMessage<Long>> countPendingReviews() {
				long count = reviewService.countPendingReviews();
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total pending reviews retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Count flagged reviews",
					description = "Returns the total count of reviews flagged for moderation"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized"),
					@ApiResponse(responseCode = "403", description = "Forbidden — Admin access required")
			})
			@GetMapping("/count/flagged")
			public ResponseEntity<CustomResponseMessage<Long>> countFlaggedReviews() {
				long count = reviewService.countFlaggedReviews();
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Total flagged reviews retrieved successfully.",
								HttpStatus.OK.value(),
								count
						)
				);
			}
			
			@Operation(
					summary = "Get average rating for a course",
					description = "Returns the average star rating of all approved reviews for a specific course"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Average rating retrieved successfully"),
					@ApiResponse(responseCode = "404", description = "Course not found"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/average-rating/course/{courseId}")
			public ResponseEntity<CustomResponseMessage<Double>> getAverageRatingForCourse(
					@PathVariable UUID courseId
			) {
				Double average = reviewService.getAverageRatingForCourse(courseId);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								"Average rating for course retrieved successfully.",
								HttpStatus.OK.value(),
								average
						)
				);
			}
			
			@Operation(
					summary = "Search and filter reviews",
					description = "Searches and filters reviews by any combination of properties with pagination and sorting support"
			)
			@ApiResponses({
					@ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
					@ApiResponse(responseCode = "401", description = "Unauthorized")
			})
			@GetMapping("/search")
			public ResponseEntity<CustomResponseMessage<Page<ReviewResponseDto>>> searchAndFilterReviews(
					
					@Parameter(description = "Keyword to search by comment, title, pros or cons")
					@RequestParam(required = false) String keyword,
					
					@Parameter(description = "Filter by course ID")
					@RequestParam(required = false) UUID courseId,
					
					@Parameter(description = "Filter by course code", example = "MATH-106")
					@RequestParam(required = false) String courseCode,
					
					@Parameter(description = "Filter by student ID")
					@RequestParam(required = false) UUID studentId,
					
					@Parameter(description = "Filter by exact rating (1-5)", example = "5")
					@RequestParam(required = false) Integer rating,
					
					@Parameter(description = "Filter by review status", example = "APPROVED")
					@RequestParam(required = false) ReviewStatus status,
					
					@Parameter(description = "Filter by approved status", example = "true")
					@RequestParam(required = false) Boolean isApproved,
					
					@Parameter(description = "Filter flagged reviews", example = "false")
					@RequestParam(required = false) Boolean isFlagged,
					
					@Parameter(description = "Filter by recommended status", example = "true")
					@RequestParam(required = false) Boolean isRecommended,
					
					@Parameter(description = "Filter edited reviews", example = "false")
					@RequestParam(required = false) Boolean isEdited,
					
					@Parameter(description = "Page number (0-based)", example = "0")
					@RequestParam(defaultValue = "0") int page,
					
					@Parameter(description = "Number of records per page", example = "10")
					@RequestParam(defaultValue = "10") int size,
					
					@Parameter(description = "Field to sort by", example = "createdAt")
					@RequestParam(defaultValue = "createdAt") String sortBy,
					
					@Parameter(description = "Sort direction: asc or desc", example = "desc")
					@RequestParam(defaultValue = "desc") String sortDir
			) {
				Page<ReviewResponseDto> data = reviewService.searchAndFilterReviews(
						keyword, courseId, courseCode, studentId, rating, status,
						isApproved, isFlagged, isRecommended, isEdited,
						page, size, sortBy, sortDir
				);
				return ResponseEntity.ok(
						new CustomResponseMessage<>(
								data.isEmpty()
										? "No reviews matched your search."
										: data.getTotalElements() + " review(s) found.",
								HttpStatus.OK.value(),
								data
						)
				);
			}
}