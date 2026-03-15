package com.leedTech.studentFeeOneTimePayment.service.otpService;

import com.leedTech.studentFeeOneTimePayment.config.emailTemplateConfig.EmailConfigTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
@Service
@RequiredArgsConstructor
public class OtpService {

			private final EmailConfigTemplate emailConfigTemplate;
			private final RedisTemplate <String, String> redisTemplate;
			
			private static final long OTP_EXPIRY_MINUTES = 5;
			private static final String OTP_PREFIX = "OTP:";
			
			public void generateAndSendOtp(String studentEmail, String studentName) {
				String otpCode = emailConfigTemplate.generateOtp();
				
				redisTemplate.opsForValue().set(
						OTP_PREFIX + studentEmail,
						otpCode,
						Duration.ofMinutes(OTP_EXPIRY_MINUTES)
				);
				emailConfigTemplate.sendOtpEmail(studentEmail, studentName, otpCode);
			}
			
			public boolean verifyOtp(String studentEmail, String otpCode) {
				String cachedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + studentEmail);
				
				if (cachedOtp != null && cachedOtp.equals(otpCode)) {
					redisTemplate.delete(OTP_PREFIX + studentEmail);
					return true;
				}
				return false;
			}
}