package com.leedTech.studentFeeOneTimePayment.dto.ai_recommendation;
import java.util.UUID;
public record CourseRecommendation(
		UUID courseId,
		String courseCode,
		String courseName,
		String department,
		String reason,
		int    matchScore
) {}