package com.leedTech.studentFeeOneTimePayment.config.firebaseConfig;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
@Slf4j
@Configuration
public class FirebaseConfig {

@Value("${firebase.project-id}")
private String projectId;

@Value("${firebase.private-key-id}")
private String privateKeyId;

@Value("${firebase.private-key}")
private String privateKey;

@Value("${firebase.client-email}")
private String clientEmail;

@Value("${firebase.client-id}")
private String clientId;

@Bean
public FirebaseApp firebaseApp() throws IOException {
	if (!FirebaseApp.getApps().isEmpty()) {
		return FirebaseApp.getInstance();
	}
	
	// Build the service account JSON dynamically from env vars
	String serviceAccountJson = """
                {
                  "type": "service_account",
                  "project_id": "%s",
                  "private_key_id": "%s",
                  "private_key": "%s",
                  "client_email": "%s",
                  "client_id": "%s",
                  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                  "token_uri": "https://oauth2.googleapis.com/token",
                  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/%s",
                  "universe_domain": "googleapis.com"
                }
                """.formatted(
			projectId,
			privateKeyId,
			privateKey.replace("\\n", "\n"),  // handle escaped newlines
			clientEmail,
			clientId,
			clientEmail.replace("@", "%40")
	);
	
	GoogleCredentials credentials = GoogleCredentials.fromStream(
			new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8))
	);
	
	FirebaseOptions options = FirebaseOptions.builder()
			                          .setCredentials(credentials)
			                          .build();
	
	FirebaseApp app = FirebaseApp.initializeApp(options);
	log.info("Firebase application initialized successfully");
	return app;
}

@Bean
public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
	return FirebaseAuth.getInstance(firebaseApp);
}
}