package com.leedTech.studentFeeOneTimePayment.controller.ai_recommendationController;
import com.leedTech.studentFeeOneTimePayment.dto.ai_recommendation.RecommendationRequest;
import com.leedTech.studentFeeOneTimePayment.dto.ai_recommendation.RecommendationResponse;
import com.leedTech.studentFeeOneTimePayment.service.ai_recommendationService.CourseRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${api.version}/ai-recommendation")
@RequiredArgsConstructor
@Tag(
		name = "AI Course Recommendation",
		description = "AI-powered course recommendation engine. Analyzes student profiles " +
				              "and suggests the most suitable courses using OpenAI GPT."
)
@SecurityRequirement(name = "bearerAuth")
public class AIRecommendationController {

		private final CourseRecommendationService courseRecommendationService;
		
		@Operation(
				summary = "Get AI course recommendations for a student",
				description = """
		                    Generates personalized course recommendations for a student based on:
		                    - Current class level and GPA
		                    - Academic year and enrollment status
		                    - Hobbies and extracurricular activities
		                    - Mandatory vs elective course priorities
		                    - Available course capacity and prerequisites
		                    
		                    Powered by OpenAI GPT-4o-mini.
		                    
		                    **Allowed roles:** ADMIN, SUPER_ADMIN, STUDENT, TEACHER, PROFESSOR,
		                    CLASS_TEACHER, HEAD_OF_DEPARTMENT, SUBSTITUTE_TEACHER, TEACHING_ASSISTANT, TUTOR
		                    """
		)
		@ApiResponses(value = {
				@ApiResponse(
						responseCode = "200",
						description = "Recommendations generated successfully",
						content = @Content(
								mediaType = MediaType.APPLICATION_JSON_VALUE,
								schema = @Schema(implementation = RecommendationResponse.class),
								examples = @ExampleObject(
										name = "Sample Response",
										value = """
		                                            {
		                                              "studentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
		                                              "studentName": "Alice Nguema",
		                                              "recommendations": [
		                                                {
		                                                  "courseId": "7cb12f64-1234-4562-b3fc-2c963f66afa6",
		                                                  "courseCode": "CS401",
		                                                  "courseName": "Data Structures & Algorithms",
		                                                  "department": "Computer Science",
		                                                  "reason": "Mandatory for Alice's class level with prerequisites already met",
		                                                  "matchScore": 10
		                                                },
		                                                {
		                                                  "courseId": "9ab34c21-5678-4562-b3fc-2c963f66afa6",
		                                                  "courseCode": "CS305",
		                                                  "courseName": "Database Systems",
		                                                  "department": "Computer Science",
		                                                  "reason": "Aligns with Alice's interest in software and strong GPA",
		                                                  "matchScore": 8
		                                                }
		                                              ],
		                                              "overallReasoning": "Prioritized mandatory courses for current class level, then electives matching student interests."
		                                            }
		                                            """
								)
						)
				),
				@ApiResponse(
						responseCode = "400",
						description = "Invalid request body",
						content = @Content(
								mediaType = MediaType.APPLICATION_JSON_VALUE,
								examples = @ExampleObject(
										value = """
		                                            {
		                                              "status": 400,
		                                              "error": "Bad Request",
		                                              "message": "numberOfRecommendations must be greater than 0"
		                                            }
		                                            """
								)
						)
				),
				@ApiResponse(
						responseCode = "404",
						description = "Student profile not found",
						content = @Content(
								mediaType = MediaType.APPLICATION_JSON_VALUE,
								examples = @ExampleObject(
										value = """
		                                            {
		                                              "status": 404,
		                                              "error": "Not Found",
		                                              "message": "Student profile not found: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
		                                            }
		                                            """
								)
						)
				),
				@ApiResponse(
						responseCode = "422",
						description = "No available courses found for the student's academic year",
						content = @Content(
								mediaType = MediaType.APPLICATION_JSON_VALUE,
								examples = @ExampleObject(
										value = """
		                                            {
		                                              "status": 422,
		                                              "error": "Unprocessable Entity",
		                                              "message": "No available courses for recommendation."
		                                            }
		                                            """
								)
						)
				),
				@ApiResponse(
						responseCode = "401",
						description = "Unauthorized — JWT token missing or invalid",
						content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
				),
				@ApiResponse(
						responseCode = "403",
						description = "Forbidden — insufficient role permissions",
						content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
				),
				@ApiResponse(
						responseCode = "500",
						description = "Internal server error — AI response parsing failed",
						content = @Content(
								mediaType = MediaType.APPLICATION_JSON_VALUE,
								examples = @ExampleObject(
										value = """
		                                            {
		                                              "status": 500,
		                                              "error": "Internal Server Error",
		                                              "message": "Failed to parse AI recommendation response"
		                                            }
		                                            """
								)
						)
				)
		})
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
				description = "Student ID, number of recommendations, and optional focus area",
				required = true,
				content = @Content(
						mediaType = MediaType.APPLICATION_JSON_VALUE,
						schema = @Schema(implementation = RecommendationRequest.class),
						examples = @ExampleObject(
								name = "Sample Request",
								value = """
		                                    {
		                                      "studentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
		                                      "numberOfRecommendations": 3,
		                                      "focus": "career"
		                                    }
		                                    """
						)
				)
		)
		@PostMapping("/recommend")
		public ResponseEntity<RecommendationResponse> getRecommendations(
				@RequestBody RecommendationRequest request) {
			return ResponseEntity.ok(courseRecommendationService.recommend(request));
		}

		
		@Operation(
				summary = "Get AI course recommendations for all active students",
				description = """
		                    Generates personalized course recommendations for every active student.
		                    Skips students with no available courses in their academic year.
		                    
		                    ⚠️ This is a heavy operation — use with caution on large datasets.
		                    
		                    **Allowed roles:** ADMIN, SUPER_ADMIN, PRINCIPAL, HEAD_OF_DEPARTMENT
		                    """
		)
		@ApiResponses(value = {
				@ApiResponse(
						responseCode = "200",
						description = "Recommendations generated for all active students",
						content = @Content(
								mediaType = MediaType.APPLICATION_JSON_VALUE,
								examples = @ExampleObject(
										name = "Sample Response",
										value = """
		                                            [
		                                              {
		                                                "studentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
		                                                "studentName": "Alice Nguema",
		                                                "recommendations": [...],
		                                                "overallReasoning": "..."
		                                              },
		                                              {
		                                                "studentId": "7cb12f64-1234-4562-b3fc-2c963f66afa6",
		                                                "studentName": "John Smith",
		                                                "recommendations": [...],
		                                                "overallReasoning": "..."
		                                              }
		                                            ]
		                                            """
								)
						)
				),
				@ApiResponse(responseCode = "422", description = "No active students found",
						content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
				@ApiResponse(responseCode = "401", description = "Unauthorized",
						content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
				@ApiResponse(responseCode = "403", description = "Forbidden",
						content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
				@ApiResponse(responseCode = "503", description = "AI service quota exceeded",
						content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
				@ApiResponse(responseCode = "500", description = "Internal server error",
						content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
		})
		@GetMapping("/recommend-all")
		public ResponseEntity< List <RecommendationResponse> > getAllRecommendations() {
			return ResponseEntity.ok(courseRecommendationService.recommendForAllStudents());
		}
		
}
