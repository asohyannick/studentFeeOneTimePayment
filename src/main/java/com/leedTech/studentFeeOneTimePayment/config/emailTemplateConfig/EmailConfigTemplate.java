package com.leedTech.studentFeeOneTimePayment.config.emailTemplateConfig;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Configuration
public class EmailConfigTemplate {

@Value("${mailtrap.api-token}")
private String apiToken;

@Value("${mailtrap.from-email}")
private String fromEmail;

@Value("${mailtrap.from-name}")
private String fromName;

// ─── Mailtrap Client Bean ────────────────────────────────────────

@Bean
public MailtrapClient mailtrapClient() {
	MailtrapConfig config = new MailtrapConfig.Builder()
			                        .token(apiToken)
			                        .build();
	return MailtrapClientFactory.createMailtrapClient(config);
}

// ─── OTP Generator ───────────────────────────────────────────────

public String generateOtp() {
	SecureRandom random = new SecureRandom();
	return String.valueOf(100000 + random.nextInt(900000));
}

// ─── Send OTP Email ──────────────────────────────────────────────

public void sendOtpEmail(String toEmail, String studentName, String otpCode) {
	try {
		MailtrapMail mail = MailtrapMail.builder()
				                    .from(new Address(fromEmail, fromName))
				                    .to(List.of(new Address(toEmail, studentName)))
				                    .subject("Your OTP Verification Code")
				                    .html(buildOtpEmailBody(studentName, otpCode))
				                    .category("OTP Verification")
				                    .build();
		
		mailtrapClient().send(mail);
		log.info("OTP email sent successfully to {}", toEmail);
		
	} catch (Exception e) {
		log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
		throw new RuntimeException("Failed to send OTP email", e);
	}
}

// ─── Send Magic Link Email ───────────────────────────────────────

public void sendMagicLinkEmail(String toEmail, String studentName, String token) {
	try {
		String magicLinkUrl = "https://yourdomain.com/api/v1/auth/magic-link/verify?token=" + token;
		
		MailtrapMail mail = MailtrapMail.builder()
				                    .from(new Address(fromEmail, fromName))
				                    .to(List.of(new Address(toEmail, studentName)))
				                    .subject("Your Magic Link Login")
				                    .html(buildMagicLinkEmailBody(studentName, magicLinkUrl))
				                    .category("Magic Link")
				                    .build();
		
		mailtrapClient().send(mail);
		log.info("Magic link email sent to {}", toEmail);
		
	} catch (Exception e) {
		log.error("Failed to send magic link email to {}: {}", toEmail, e.getMessage());
		throw new RuntimeException("Failed to send magic link email", e);
	}
}

// ─── OTP HTML Template ───────────────────────────────────────────

private String buildOtpEmailBody(String studentName, String otpCode) {
	return """
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background-color: #ffffff;
                                padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <h2 style="color: #333333;">Hello, %s 👋</h2>
                        <p style="color: #555555;">Use the OTP code below to complete your verification.</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <span style="font-size: 36px; font-weight: bold; color: #4CAF50;
                                         letter-spacing: 8px; padding: 10px 20px;
                                         background-color: #f0fff0; border-radius: 8px;">
                                %s
                            </span>
                        </div>
                        <p style="color: #ff0000; font-size: 13px;">
                            ⚠️ This OTP expires in 5 minutes. Do not share it with anyone.
                        </p>
                        <hr style="border: none; border-top: 1px solid #eeeeee; margin: 20px 0;">
                        <p style="color: #aaaaaa; font-size: 12px;">
                            If you did not request this, please ignore this email.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(studentName, otpCode);
}

// ─── Magic Link HTML Template ────────────────────────────────────

private String buildMagicLinkEmailBody(String studentName, String magicLinkUrl) {
	return """
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background-color: #ffffff;
                                padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <h2 style="color: #333333;">Hello, %s 👋</h2>
                        <p style="color: #555555;">Click the button below to log in instantly. No password needed.</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #4CAF50; color: white; padding: 14px 28px;
                                                text-decoration: none; border-radius: 6px; font-size: 16px;">
                                Login with Magic Link
                            </a>
                        </div>
                        <p style="color: #ff0000; font-size: 13px;">⚠️ This link expires in 15 minutes.</p>
                        <hr style="border: none; border-top: 1px solid #eeeeee; margin: 20px 0;">
                        <p style="color: #aaaaaa; font-size: 12px;">
                            If you did not request this, please ignore this email.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(studentName, magicLinkUrl);
}
}