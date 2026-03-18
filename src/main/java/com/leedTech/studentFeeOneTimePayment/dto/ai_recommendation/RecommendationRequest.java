package com.leedTech.studentFeeOneTimePayment.dto.ai_recommendation;
import java.util.UUID;
public record RecommendationRequest(
		UUID studentId,
		int numberOfRecommendations,
		String focus
) {}