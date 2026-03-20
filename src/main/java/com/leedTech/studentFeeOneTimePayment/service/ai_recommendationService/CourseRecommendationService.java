package com.leedTech.studentFeeOneTimePayment.service.ai_recommendationService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leedTech.studentFeeOneTimePayment.constant.CourseStatus;
import com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus;
import com.leedTech.studentFeeOneTimePayment.dto.ai_recommendation.CourseRecommendation;
import com.leedTech.studentFeeOneTimePayment.dto.ai_recommendation.RecommendationRequest;
import com.leedTech.studentFeeOneTimePayment.dto.ai_recommendation.RecommendationResponse;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.exception.UnAuthorizedException;
import com.leedTech.studentFeeOneTimePayment.repository.courseRepository.CourseRepository;
import com.leedTech.studentFeeOneTimePayment.repository.studentProfileRepository.StudentProfileRepository;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRecommendationService {

		private final OpenAIClient openAIClient;
		private final StudentProfileRepository studentProfileRepository;
		private final CourseRepository courseRepository;
		private final CourseRecommendationPromptBuilder promptBuilder;
		private final ObjectMapper objectMapper;
		
		@Value("${openai.model:gpt-3.5-turbo}")
		private String model;
		
		@Value("${openai.temperature:0.4}")
		private Double temperature;
		
		@Value ("${openai.max-tokens:2000}")
		private Integer maxTokens;
		
		public RecommendationResponse recommend( RecommendationRequest request) {
			
			StudentProfile profile = studentProfileRepository
					                         .findByStudentIdAndIsDeletedFalse(request.studentId())
					                         .orElseThrow(() -> new NotFoundException(
							                         "Student profile not found: " + request.studentId()));
			
			List < Course > availableCourses = courseRepository.findRecommendableCourses(
					profile.getAcademicYear(),
					List.of( CourseStatus.UPCOMING, CourseStatus.ONGOING)
			);
			
			if (availableCourses.isEmpty()) {
				throw new NotFoundException("No available courses for recommendation.");
			}
			
			int count = Math.min(request.numberOfRecommendations(), availableCourses.size());
			
			String prompt = promptBuilder.build(
					profile,
					availableCourses,
					count,
					request.focus()
			);
			
			String rawJson = callOpenAI(prompt);
			return parseAndEnrich(rawJson, profile);
		}
		
		private String callOpenAI(String prompt) {
			try {
				ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
						                                    .model(model)
						                                    .addUserMessage(prompt)
						                                    .maxCompletionTokens(maxTokens)
						                                    .temperature(temperature)
						                                    .build();
				
				ChatCompletion completion = openAIClient
						                            .chat()
						                            .completions()
						                            .create(params);
				
				return completion.choices().stream()
						       .findFirst()
						       .flatMap(choice -> choice.message().content())
						       .orElseThrow(() -> new BadRequestException("Empty response from OpenAI"));
				
			} catch (Exception e) {
				String message = e.getMessage();
				if (message != null && message.contains("429")) {
					throw new BadRequestException(
							"AI service is temporarily unavailable due to quota limits. " +
									"Please try again later or contact the administrator."
					);
				}
				if (message != null && message.contains("401")) {
					throw new UnAuthorizedException("Invalid OpenAI API key. Please check configuration.");
				}
				throw new BadRequestException("Failed to contact AI service: " + message);
			}
		}
		
		private RecommendationResponse parseAndEnrich(String rawJson, StudentProfile profile) {
			try {
				String clean = rawJson
						               .replaceAll("(?s)```json\\s*", "")
						               .replaceAll("(?s)```\\s*", "")
						               .trim();
				
				JsonNode root = objectMapper.readTree(clean);
				
				List< CourseRecommendation > recommendations = new ArrayList <> ();
				
				for (JsonNode node : root.get("recommendations")) {
					recommendations.add(new CourseRecommendation(
							UUID.fromString(node.get("courseId").asText()),
							node.get("courseCode").asText(),
							node.get("courseName").asText(),
							node.get("department").asText(),
							node.get("reason").asText(),
							node.get("matchScore").asInt()
					));
				}
				
				recommendations.sort(
						Comparator.comparingInt(CourseRecommendation::matchScore).reversed()
				);
				
				User student = profile.getStudent();
				return new RecommendationResponse(
						profile.getId(),
						student.getFirstName() + " " + student.getLastName(),
						recommendations,
						root.get("overallReasoning").asText()
				);
				
			} catch ( JsonProcessingException e) {
				log.error("Failed to parse OpenAI response:\n{}", rawJson, e);
				throw new BadRequestException("Failed to parse AI recommendation response", e);
			}
		}

		public List<RecommendationResponse> recommendForAllStudents() {
			
			List<StudentProfile> activeStudents = studentProfileRepository.findAllActiveStudents(EnrollmentStatus.ACTIVE);
			
			if (activeStudents.isEmpty()) {
				throw new IllegalStateException("No active students found.");
			}
			
			List<RecommendationResponse> responses = new ArrayList<>();
			
			for (StudentProfile profile : activeStudents) {
				
				try {
					List<Course> availableCourses = courseRepository.findRecommendableCourses(
							profile.getAcademicYear(),
							List.of(CourseStatus.UPCOMING, CourseStatus.ONGOING)
					);
					
					if (availableCourses.isEmpty()) {
						log.warn("No available courses for student: {}", profile.getStudentNumber());
						continue;
					}
					
					int count = Math.min(3, availableCourses.size());
					
					String prompt = promptBuilder.build(
							profile,
							availableCourses,
							count,
							null
					);
					
					String rawJson = callOpenAI(prompt);
					responses.add(parseAndEnrich(rawJson, profile));
					
				} catch (Exception e) {
					log.error("Failed to generate recommendations for student {}: {}",
							profile.getStudentNumber(), e.getMessage());
				}
			}
			
			if (responses.isEmpty()) {
				throw new BadRequestException("Could not generate recommendations for any student.");
			}
			
			return responses;
		}
}