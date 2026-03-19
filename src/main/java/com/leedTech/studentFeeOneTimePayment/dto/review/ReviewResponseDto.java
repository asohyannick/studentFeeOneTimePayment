package com.leedTech.studentFeeOneTimePayment.dto.review;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.leedTech.studentFeeOneTimePayment.constant.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
@JsonInclude (JsonInclude.Include.NON_NULL)
@Schema(name = "ReviewResponse", description = "Response payload for review data")
public record ReviewResponseDto(
		
		@Schema(description = "Unique review ID")
		UUID id,
		
		@Schema(description = "ID of the reviewed course")
		UUID courseId,
		
		@Schema(description = "Name of the reviewed course")
		String courseName,
		
		@Schema(description = "Code of the reviewed course")
		String courseCode,
		
		@Schema(description = "ID of the student who wrote the review")
		UUID studentId,
		
		@Schema(description = "First name of the student")
		String studentFirstName,
		
		@Schema(description = "Last name of the student")
		String studentLastName,
		
		@Schema(description = "Full name of the student")
		String studentFullName,
		
		@Schema(description = "Email of the student")
		String studentEmail,
		
		@Schema(description = "Rating from 1 to 5 stars", example = "4")
		int rating,
		
		@Schema(description = "Main review comment")
		String comment,
		
		@Schema(description = "Short review title")
		String title,
		
		@Schema(description = "What the student liked")
		String pros,
		
		@Schema(description = "What could be improved")
		String cons,
		
		@Schema(description = "Whether the student recommends this course")
		boolean isRecommended,
		
		@Schema(description = "Number of students who found this review helpful")
		int helpfulCount,
		
		@Schema(description = "Number of times this review was reported")
		int reportCount,
		
		@Schema(description = "Review moderation status")
		ReviewStatus status,
		
		@Schema(description = "Whether review is approved")
		boolean approved,
		
		@Schema(description = "Whether review is flagged for moderation")
		boolean flagged,
		
		@Schema(description = "Reason for rejection if applicable")
		String rejectionReason,
		
		@Schema(description = "Internal admin note")
		String adminNote,
		
		@Schema(description = "Whether review was edited after posting")
		boolean edited,
		
		@Schema(description = "When the review was last edited")
		Instant editedAt,
		
		@Schema(description = "When the review was created")
		Instant createdAt,
		
		@Schema(description = "When the review was last updated")
		Instant updatedAt

) {}