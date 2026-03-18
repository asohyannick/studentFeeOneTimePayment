package com.leedTech.studentFeeOneTimePayment.dto.ai_recommendation;

import java.util.List;
import java.util.UUID;

public record RecommendationResponse(
		UUID studentId,
		String studentName,
		List<CourseRecommendation> recommendations,
		String overallReasoning
) {}