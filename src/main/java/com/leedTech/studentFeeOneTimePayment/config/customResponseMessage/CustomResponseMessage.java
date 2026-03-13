package com.leedTech.studentFeeOneTimePayment.config.customResponseMessage;

public record CustomResponseMessage<T>(
		String message,
		int statusCode,
		T data
) {}
