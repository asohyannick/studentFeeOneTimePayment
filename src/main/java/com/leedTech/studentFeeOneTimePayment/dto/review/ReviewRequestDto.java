package com.leedTech.studentFeeOneTimePayment.dto.review;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.UUID;
@Schema(name = "ReviewRequest", description = "Request payload for creating or updating a review")
public record ReviewRequestDto(
		
		@Schema(description = "UUID of the course being reviewed", example = "a3f1c2d4-12ab-4e56-89cd-abcdef012345")
		@NotNull(message = "Course ID is required")
		UUID courseId,
		
		@Schema(description = "UUID of the student submitting the review", example = "b4e2d3f5-23bc-5f67-90de-bcdef0123456")
		@NotNull(message = "Student ID is required")
		UUID studentId,
		
		@Schema(description = "Rating from 1 to 5 stars", example = "4")
		@NotNull(message = "Rating is required")
		@Min(value = 1, message = "Rating must be at least 1")
		@Max(value = 5, message = "Rating must not exceed 5")
		Integer rating,
		
		@Schema(description = "Main review comment", example = "This course was very insightful and well structured.")
		@NotBlank(message = "Comment is required")
		@Size(min = 10, max = 2000, message = "Comment must be between 10 and 2000 characters")
		String comment,
		
		@Schema(description = "Short review title", example = "Excellent course!")
		@Size(max = 255, message = "Title must not exceed 255 characters")
		String title,
		
		@Schema(description = "What the student liked about the course", example = "Clear explanations, practical examples")
		@Size(max = 1000, message = "Pros must not exceed 1000 characters")
		String pros,
		
		@Schema(description = "What could be improved", example = "More exercises would be helpful")
		@Size(max = 1000, message = "Cons must not exceed 1000 characters")
		String cons,
		
		@Schema(description = "Whether the student recommends this course", example = "true")
		boolean recommended

) {}