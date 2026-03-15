package com.leedTech.studentFeeOneTimePayment.config.emailTemplateConfig;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Slf4j
@Component
@RequiredArgsConstructor
			public class EmailConfigTemplate {
			
			private final JavaMailSender mailSender;
			
			@Value("${mailtrap.from-email}")
			private String fromEmail;
			
			@Value("${mailtrap.from-name}")
			private String fromName;
			
			public String generateOtp() {
				SecureRandom random = new SecureRandom();
				return String.valueOf(100000 + random.nextInt(900000));
			}
			
			public void sendOtpEmail(String toEmail, String studentName, String otpCode) {
				try {
					MimeMessage message = mailSender.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
					
					helper.setFrom(fromEmail, fromName);
					helper.setTo(toEmail);
					helper.setSubject("🔐 Your OTP Verification Code – LeedTech");
					helper.setText(buildOtpEmailBody(studentName, otpCode), true);
					
					mailSender.send(message);
					log.info("OTP email sent successfully to {}", toEmail);
					
				} catch (Exception e) {
					log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
					throw new RuntimeException("Failed to send OTP email", e);
				}
			}
			
			public void sendMagicLinkEmail(String toEmail, String studentName, String token) {
				try {
					String magicLinkUrl = "https://yourdomain.com/api/v1/auth/magic-link/verify?token=" + token;
					
					MimeMessage message = mailSender.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
					
					helper.setFrom(fromEmail, fromName);
					helper.setTo(toEmail);
					helper.setSubject("✨ Your Magic Link Login – LeedTech");
					helper.setText(buildMagicLinkEmailBody(studentName, magicLinkUrl), true);
					
					mailSender.send(message);
					log.info("Magic link email sent to {}", toEmail);
					
				} catch (Exception e) {
					log.error("Failed to send magic link email to {}: {}", toEmail, e.getMessage());
					throw new RuntimeException("Failed to send magic link email", e);
				}
			}
			
			private String buildOtpEmailBody(String studentName, String otpCode) {
				return """
			                <!DOCTYPE html>
			                <html lang="en">
			                <head>
			                    <meta charset="UTF-8" />
			                    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
			                    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
			                    <title>OTP Verification – LeedTech</title>
			                    <style>
			                        * { margin: 0; padding: 0; box-sizing: border-box; }
			                        body {
			                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
			                            background-color: #f0f4f8;
			                            padding: 40px 20px;
			                            color: #333333;
			                        }
			                        .wrapper { max-width: 620px; margin: 0 auto; }
			                        .card {
			                            background-color: #ffffff;
			                            border-radius: 16px;
			                            overflow: hidden;
			                            box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
			                        }
			                        .header {
			                            background: linear-gradient(135deg, #1a73e8 0%%, #0d47a1 100%%);
			                            padding: 40px 40px 32px;
			                            text-align: center;
			                        }
			                        .header .logo { font-size: 28px; font-weight: 800; color: #ffffff; letter-spacing: 1px; }
			                        .header .logo span { color: #90caf9; }
			                        .header .tagline { color: #bbdefb; font-size: 13px; margin-top: 6px; letter-spacing: 0.5px; }
			                        .body { padding: 40px; }
			                        .greeting { font-size: 22px; font-weight: 700; color: #1a1a2e; margin-bottom: 12px; }
			                        .message { font-size: 15px; color: #555555; line-height: 1.7; margin-bottom: 32px; }
			                        .otp-section {
			                            background: linear-gradient(135deg, #e3f2fd 0%%, #f3e5f5 100%%);
			                            border: 2px dashed #1a73e8;
			                            border-radius: 12px;
			                            padding: 28px;
			                            text-align: center;
			                            margin-bottom: 32px;
			                        }
			                        .otp-label { font-size: 12px; font-weight: 600; color: #1a73e8; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 12px; }
			                        .otp-code { font-size: 48px; font-weight: 900; color: #0d47a1; letter-spacing: 12px; font-family: 'Courier New', monospace; }
			                        .otp-timer { margin-top: 12px; font-size: 13px; color: #e53935; font-weight: 600; }
			                        .divider { height: 1px; background: linear-gradient(to right, transparent, #e0e0e0, transparent); margin: 28px 0; }
			                        .info-box { background-color: #fff8e1; border-left: 4px solid #f9a825; border-radius: 6px; padding: 14px 18px; margin-bottom: 28px; }
			                        .info-box p { font-size: 13px; color: #795548; line-height: 1.6; }
			                        .steps { margin-bottom: 28px; }
			                        .steps h4 { font-size: 14px; font-weight: 700; color: #333; margin-bottom: 12px; }
			                        .step { display: flex; align-items: flex-start; margin-bottom: 10px; gap: 10px; }
			                        .step-number {
			                            background-color: #1a73e8; color: white; width: 22px; height: 22px;
			                            border-radius: 50%%; font-size: 12px; font-weight: 700;
			                            display: flex; align-items: center; justify-content: center;
			                            flex-shrink: 0; margin-top: 1px;
			                        }
			                        .step-text { font-size: 14px; color: #555; line-height: 1.5; }
			                        .footer { background-color: #f8f9fa; padding: 24px 40px; text-align: center; border-top: 1px solid #eeeeee; }
			                        .footer p { font-size: 12px; color: #9e9e9e; line-height: 1.8; }
			                        .footer .brand { font-weight: 700; color: #1a73e8; }
			                        .footer .links { margin-top: 10px; }
			                        .footer .links a { color: #1a73e8; text-decoration: none; font-size: 12px; margin: 0 8px; }
			                    </style>
			                </head>
			                <body>
			                    <div class="wrapper">
			                        <div class="card">
			                            <div class="header">
			                                <div class="logo">Leed<span>Tech</span></div>
			                                <div class="tagline">Student Management Platform</div>
			                            </div>
			                            <div class="body">
			                                <p class="greeting">Hello, %s 👋</p>
			                                <p class="message">
			                                    We received a request to verify your identity on the LeedTech
			                                    Student Management Platform. Use the One-Time Password (OTP) below
			                                    to complete your verification. This code is valid for <strong>5 minutes</strong>.
			                                </p>
			                                <div class="otp-section">
			                                    <div class="otp-label">🔐 Your Verification Code</div>
			                                    <div class="otp-code">%s</div>
			                                    <div class="otp-timer">⏱ Expires in 5 minutes</div>
			                                </div>
			                                <div class="steps">
			                                    <h4>How to use your OTP:</h4>
			                                    <div class="step">
			                                        <div class="step-number">1</div>
			                                        <div class="step-text">Return to the LeedTech verification page</div>
			                                    </div>
			                                    <div class="step">
			                                        <div class="step-number">2</div>
			                                        <div class="step-text">Enter the 6-digit code shown above</div>
			                                    </div>
			                                    <div class="step">
			                                        <div class="step-number">3</div>
			                                        <div class="step-text">Click <strong>Verify</strong> to complete your login</div>
			                                    </div>
			                                </div>
			                                <div class="info-box">
			                                    <p>⚠️ <strong>Security Notice:</strong> Never share this code with anyone —
			                                    including LeedTech staff. Our team will never ask for your OTP.
			                                    If you did not request this code, please secure your account immediately.</p>
			                                </div>
			                                <div class="divider"></div>
			                                <p style="font-size: 13px; color: #9e9e9e; text-align: center;">
			                                    Didn't request this? You can safely ignore this email. Your account remains secure.
			                                </p>
			                            </div>
			                            <div class="footer">
			                                <p>© 2026 <span class="brand">LeedTech</span> — Student Management Platform<br/>
			                                This is an automated message. Please do not reply to this email.</p>
			                                <div class="links">
			                                    <a href="#">Privacy Policy</a>
			                                    <a href="#">Terms of Service</a>
			                                    <a href="#">Support</a>
			                                </div>
			                            </div>
			                        </div>
			                    </div>
			                </body>
			                </html>
			                """.formatted(studentName, otpCode);
			}
			
			private String buildMagicLinkEmailBody(String studentName, String magicLinkUrl) {
				return """
			                <!DOCTYPE html>
			                <html lang="en">
			                <head>
			                    <meta charset="UTF-8" />
			                    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
			                    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
			                    <title>Magic Link Login – LeedTech</title>
			                    <style>
			                        * { margin: 0; padding: 0; box-sizing: border-box; }
			                        body {
			                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
			                            background-color: #f0f4f8;
			                            padding: 40px 20px;
			                            color: #333333;
			                        }
			                        .wrapper { max-width: 620px; margin: 0 auto; }
			                        .card { background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 8px 30px rgba(0,0,0,0.08); }
			                        .header {
			                            background: linear-gradient(135deg, #6a11cb 0%%, #2575fc 100%%);
			                            padding: 40px 40px 32px;
			                            text-align: center;
			                        }
			                        .header .logo { font-size: 28px; font-weight: 800; color: #ffffff; letter-spacing: 1px; }
			                        .header .logo span { color: #ce93d8; }
			                        .header .tagline { color: #e1bee7; font-size: 13px; margin-top: 6px; }
			                        .header .icon { font-size: 52px; margin-top: 20px; }
			                        .body { padding: 40px; }
			                        .greeting { font-size: 22px; font-weight: 700; color: #1a1a2e; margin-bottom: 12px; }
			                        .message { font-size: 15px; color: #555555; line-height: 1.7; margin-bottom: 32px; }
			                        .btn-container { text-align: center; margin: 32px 0; }
			                        .btn-magic {
			                            display: inline-block;
			                            background: linear-gradient(135deg, #6a11cb 0%%, #2575fc 100%%);
			                            color: #ffffff; text-decoration: none; font-size: 16px;
			                            font-weight: 700; padding: 16px 40px; border-radius: 50px;
			                            letter-spacing: 0.5px; box-shadow: 0 4px 20px rgba(106,17,203,0.35);
			                        }
			                        .timer-badge {
			                            display: inline-block; background-color: #fce4ec; color: #c62828;
			                            font-size: 13px; font-weight: 600; padding: 6px 16px;
			                            border-radius: 50px; margin-top: 14px;
			                        }
			                        .url-box {
			                            background-color: #f5f5f5; border: 1px solid #e0e0e0;
			                            border-radius: 8px; padding: 14px 18px; margin: 24px 0; word-break: break-all;
			                        }
			                        .url-box p { font-size: 11px; color: #9e9e9e; margin-bottom: 6px; text-transform: uppercase; letter-spacing: 1px; font-weight: 600; }
			                        .url-box a { font-size: 12px; color: #2575fc; text-decoration: none; word-break: break-all; }
			                        .divider { height: 1px; background: linear-gradient(to right, transparent, #e0e0e0, transparent); margin: 28px 0; }
			                        .info-box { background-color: #fff8e1; border-left: 4px solid #f9a825; border-radius: 6px; padding: 14px 18px; margin-bottom: 28px; }
			                        .info-box p { font-size: 13px; color: #795548; line-height: 1.6; }
			                        .footer { background-color: #f8f9fa; padding: 24px 40px; text-align: center; border-top: 1px solid #eeeeee; }
			                        .footer p { font-size: 12px; color: #9e9e9e; line-height: 1.8; }
			                        .footer .brand { font-weight: 700; color: #6a11cb; }
			                        .footer .links { margin-top: 10px; }
			                        .footer .links a { color: #6a11cb; text-decoration: none; font-size: 12px; margin: 0 8px; }
			                    </style>
			                </head>
			                <body>
			                    <div class="wrapper">
			                        <div class="card">
			                            <div class="header">
			                                <div class="logo">Leed<span>Tech</span></div>
			                                <div class="tagline">Student Management Platform</div>
			                                <div class="icon">✨</div>
			                            </div>
			                            <div class="body">
			                                <p class="greeting">Hello, %s 👋</p>
			                                <p class="message">
			                                    You requested a <strong>Magic Link</strong> to sign in to your LeedTech
			                                    account. Click the button below to log in instantly — no password required.
			                                    This link is valid for <strong>50 minutes</strong> and can only be used once.
			                                </p>
			                                <div class="btn-container">
			                                    <a href="%s" class="btn-magic">✨ &nbsp; Login with Magic Link</a>
			                                    <br/>
			                                    <span class="timer-badge">⏱ Expires in 5 minutes</span>
			                                </div>
			                                <div class="url-box">
			                                    <p>Or copy and paste this link into your browser</p>
			                                    <a href="%s">%s</a>
			                                </div>
			                                <div class="divider"></div>
			                                <div class="info-box">
			                                    <p>⚠️ <strong>Security Notice:</strong> This link grants immediate access to
			                                    your LeedTech account. Never forward this email or share this link with anyone.
			                                    If you did not request this login, please ignore this email — your account remains secure.</p>
			                                </div>
			                                <p style="font-size: 13px; color: #9e9e9e; text-align: center;">
			                                    Didn't request this? You can safely ignore this email.
			                                </p>
			                            </div>
			                            <div class="footer">
			                                <p>© 2026 <span class="brand">LeedTech</span> — Student Management Platform<br/>
			                                This is an automated message. Please do not reply to this email.</p>
			                                <div class="links">
			                                    <a href="#">Privacy Policy</a>
			                                    <a href="#">Terms of Service</a>
			                                    <a href="#">Support</a>
			                                </div>
			                            </div>
			                        </div>
			                    </div>
			                </body>
			                </html>
			                """.formatted(studentName, magicLinkUrl, magicLinkUrl, magicLinkUrl);
			}
			
			public void sendPasswordResetOtpEmail(String toEmail, String studentName, String otpCode) {
				try {
					MimeMessage message = mailSender.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
					
					helper.setFrom(fromEmail, fromName);
					helper.setTo(toEmail);
					helper.setSubject("🔑 Password Reset OTP – LeedTech");
					helper.setText(buildPasswordResetEmailBody(studentName, otpCode), true);
					
					mailSender.send(message);
					log.info("Password reset OTP email sent to {}", toEmail);
					
				} catch (Exception e) {
					log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
					throw new RuntimeException("Failed to send password reset email", e);
				}
			}
			
			private String buildPasswordResetEmailBody(String studentName, String otpCode) {
				return """
			            <!DOCTYPE html>
			            <html lang="en">
			            <head>
			                <meta charset="UTF-8" />
			                <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
			                <title>Password Reset – LeedTech</title>
			                <style>
			                    * { margin: 0; padding: 0; box-sizing: border-box; }
			                    body {
			                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
			                        background-color: #f0f4f8;
			                        padding: 40px 20px;
			                        color: #333333;
			                    }
			                    .wrapper { max-width: 620px; margin: 0 auto; }
			                    .card {
			                        background-color: #ffffff;
			                        border-radius: 16px;
			                        overflow: hidden;
			                        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
			                    }
			                    .header {
			                        background: linear-gradient(135deg, #e53935 0%%, #b71c1c 100%%);
			                        padding: 40px 40px 32px;
			                        text-align: center;
			                    }
			                    .header .logo { font-size: 28px; font-weight: 800; color: #ffffff; letter-spacing: 1px; }
			                    .header .logo span { color: #ef9a9a; }
			                    .header .tagline { color: #ffcdd2; font-size: 13px; margin-top: 6px; }
			                    .header .icon { font-size: 52px; margin-top: 20px; }
			                    .body { padding: 40px; }
			                    .greeting { font-size: 22px; font-weight: 700; color: #1a1a2e; margin-bottom: 12px; }
			                    .message { font-size: 15px; color: #555555; line-height: 1.7; margin-bottom: 32px; }
			                    .otp-section {
			                        background: linear-gradient(135deg, #ffebee 0%%, #fce4ec 100%%);
			                        border: 2px dashed #e53935;
			                        border-radius: 12px;
			                        padding: 28px;
			                        text-align: center;
			                        margin-bottom: 32px;
			                    }
			                    .otp-label { font-size: 12px; font-weight: 600; color: #e53935; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 12px; }
			                    .otp-code { font-size: 48px; font-weight: 900; color: #b71c1c; letter-spacing: 12px; font-family: 'Courier New', monospace; }
			                    .otp-timer { margin-top: 12px; font-size: 13px; color: #e53935; font-weight: 600; }
			                    .divider { height: 1px; background: linear-gradient(to right, transparent, #e0e0e0, transparent); margin: 28px 0; }
			                    .info-box { background-color: #fff8e1; border-left: 4px solid #f9a825; border-radius: 6px; padding: 14px 18px; margin-bottom: 28px; }
			                    .info-box p { font-size: 13px; color: #795548; line-height: 1.6; }
			                    .footer { background-color: #f8f9fa; padding: 24px 40px; text-align: center; border-top: 1px solid #eeeeee; }
			                    .footer p { font-size: 12px; color: #9e9e9e; line-height: 1.8; }
			                    .footer .brand { font-weight: 700; color: #e53935; }
			                    .footer .links { margin-top: 10px; }
			                    .footer .links a { color: #e53935; text-decoration: none; font-size: 12px; margin: 0 8px; }
			                </style>
			            </head>
			            <body>
			                <div class="wrapper">
			                    <div class="card">
			                        <div class="header">
			                            <div class="logo">Leed<span>Tech</span></div>
			                            <div class="tagline">Student Management Platform</div>
			                            <div class="icon">🔑</div>
			                        </div>
			                        <div class="body">
			                            <p class="greeting">Hello, %s 👋</p>
			                            <p class="message">
			                                We received a request to <strong>reset your password</strong> on the
			                                LeedTech Student Management Platform. Use the OTP below to proceed.
			                                This code is valid for <strong>5 minutes</strong>.
			                            </p>
			                            <div class="otp-section">
			                                <div class="otp-label">🔑 Password Reset Code</div>
			                                <div class="otp-code">%s</div>
			                                <div class="otp-timer">⏱ Expires in 5 minutes</div>
			                            </div>
			                            <div class="info-box">
			                                <p>⚠️ <strong>Security Notice:</strong> If you did not request a password
			                                reset, please ignore this email and secure your account immediately.
			                                Never share this code with anyone — including LeedTech staff.</p>
			                            </div>
			                            <div class="divider"></div>
			                            <p style="font-size: 13px; color: #9e9e9e; text-align: center;">
			                                Didn't request this? You can safely ignore this email. Your account remains secure.
			                            </p>
			                        </div>
			                        <div class="footer">
			                            <p>© 2026 <span class="brand">LeedTech</span> — Student Management Platform<br/>
			                            This is an automated message. Please do not reply to this email.</p>
			                            <div class="links">
			                                <a href="#">Privacy Policy</a>
			                                <a href="#">Terms of Service</a>
			                                <a href="#">Support</a>
			                            </div>
			                        </div>
			                    </div>
			                </div>
			            </body>
			            </html>
			            """.formatted(studentName, otpCode);
			}
}