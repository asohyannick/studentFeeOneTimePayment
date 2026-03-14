package com.leedTech.studentFeeOneTimePayment.config.cloudinaryConfig;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

		@Value("${cloudinary.cloud-name}")
		private String cloudName;
		
		@Value("${cloudinary.api-key}")
		private String apiKey;
		
		@Value("${cloudinary.api-secret}")
		private String apiSecret;
		
		private static final String BASE_FOLDER = "student-fee-payment";
		
		public static final long MAX_IMAGE_SIZE    = 5  * 1024 * 1024L;
		public static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024L;
		public static final long MAX_VIDEO_SIZE    = 50 * 1024 * 1024L;
		
		public static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
				"image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
		);
		public static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
				"application/pdf",
				"application/msword",
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				"application/vnd.ms-excel",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
		);
		public static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
				"video/mp4", "video/mpeg", "video/quicktime", "video/webm"
		);
		
		@Bean
		public Cloudinary cloudinary() {
			return new Cloudinary(ObjectUtils.asMap(
					"cloud_name", cloudName,
					"api_key",    apiKey,
					"api_secret", apiSecret,
					"secure",     true
			));
		}
		
		public String uploadProfilePicture(MultipartFile file, UUID entityId) {
			validateImage(file);
			return upload(
					file,
					BASE_FOLDER + "/profile-pictures",
					"profile_" + entityId,
					ObjectUtils.asMap(
							"transformation", "c_fill,w_400,h_400,g_face,q_auto,f_auto",
							"resource_type",  "image"
					)
			);
		}
		
		public String uploadCourseImage(MultipartFile file, UUID courseId) {
			validateImage(file);
			return upload(
					file,
					BASE_FOLDER + "/course-images",
					"course_" + courseId,
					ObjectUtils.asMap(
							"transformation", "c_fill,w_1280,h_720,q_auto,f_auto",
							"resource_type",  "image"
					)
			);
		}
		
		public String uploadDocument(MultipartFile file, String subFolder, String publicIdPrefix) {
			validateDocument(file);
			return upload(
					file,
					BASE_FOLDER + "/" + subFolder,
					publicIdPrefix + "_" + UUID.randomUUID(),
					ObjectUtils.asMap(
							"resource_type", "raw",   // raw = non-image/video files
							"format",        extractExtension(file)
					)
			);
		}
		
		public String uploadVideo(MultipartFile file, String subFolder, String publicIdPrefix) {
			validateVideo(file);
			return upload(
					file,
					BASE_FOLDER + "/" + subFolder,
					publicIdPrefix + "_" + UUID.randomUUID(),
					ObjectUtils.asMap(
							"resource_type", "video",
							"eager",         "c_fill,w_1280,h_720,q_auto"
					)
			);
		}
		
		public String uploadImage(MultipartFile file, String subFolder, String publicIdPrefix) {
			validateImage(file);
			return upload(
					file,
					BASE_FOLDER + "/" + subFolder,
					publicIdPrefix + "_" + UUID.randomUUID(),
					ObjectUtils.asMap(
							"resource_type", "image",
							"quality",       "auto",
							"fetch_format",  "auto"
					)
			);
		}
		
		public void deleteByUrl(String fileUrl) {
			if (fileUrl == null || fileUrl.isBlank()) return;
			try {
				String publicId = extractPublicId(fileUrl);
				
				if (publicId == null || publicId.isBlank()) {
					log.warn("Skipping Cloudinary delete — could not extract public ID from: {}", fileUrl);
					return;
				}
				
				String resourceType = detectResourceTypeFromUrl(fileUrl);
				cloudinary().uploader().destroy(
						publicId,
						ObjectUtils.asMap("resource_type", resourceType)
				);
				log.info("Deleted from Cloudinary: {}", publicId);
				
			} catch (IOException e) {
				log.warn("Failed to delete from Cloudinary: {}", e.getMessage());
			}
		}

		private String extractPublicId(String fileUrl) {
			try {
				if (fileUrl == null || fileUrl.isBlank()) return null;
				
				String cleanUrl = fileUrl.split("\\?")[0];
				
				String withoutExtension = cleanUrl.contains(".")
						                          ? cleanUrl.substring(0, cleanUrl.lastIndexOf('.'))
						                          : cleanUrl;
				
				int folderIndex = withoutExtension.indexOf(BASE_FOLDER);
				if (folderIndex != -1) {
					return withoutExtension.substring(folderIndex);
				}
				
				int uploadIndex = withoutExtension.indexOf("/upload/");
				if (uploadIndex != -1) {
					String afterUpload = withoutExtension.substring(uploadIndex + 8);
					if (afterUpload.matches("v\\d+/.*")) {
						return afterUpload.substring(afterUpload.indexOf('/') + 1);
					}
					return afterUpload;
				}
				
				log.warn("Could not extract public ID from URL: {}", fileUrl);
				return null;
				
			} catch (Exception e) {
				log.warn("Failed to extract public ID from URL: {} — {}", fileUrl, e.getMessage());
				return null;
			}
		}
		
		public void validateImage(MultipartFile file) {
			validateNotEmpty(file);
			validateSize(file, MAX_IMAGE_SIZE, "Image");
			validateMimeType(file, ALLOWED_IMAGE_TYPES,
					"Only JPEG, JPG, PNG, WEBP and GIF images are allowed"
			);
		}
		
		public void validateDocument(MultipartFile file) {
			validateNotEmpty(file);
			validateSize(file, MAX_DOCUMENT_SIZE, "Document");
			validateMimeType(file, ALLOWED_DOCUMENT_TYPES,
					"Only PDF, Word (.doc/.docx) and Excel (.xls/.xlsx) files are allowed"
			);
		}
		
		public void validateVideo(MultipartFile file) {
			validateNotEmpty(file);
			validateSize(file, MAX_VIDEO_SIZE, "Video");
			validateMimeType(file, ALLOWED_VIDEO_TYPES,
					"Only MP4, MPEG, MOV and WEBM videos are allowed"
			);
		}
		
		private String upload(
				MultipartFile file,
				String folder,
				String publicId,
				Map<String, Object> extraOptions
		) {
			try {
				Map<String, Object> options = ObjectUtils.asMap(
						"folder",     folder,
						"public_id",  publicId,
						"overwrite",  true
				);
				options.putAll(extraOptions);
				
				Map<?, ?> result = cloudinary().uploader().upload(file.getBytes(), options);
				String url = (String) result.get("secure_url");
				
				log.info("Uploaded to Cloudinary: folder={} public_id={}", folder, publicId);
				return url;
				
			} catch (IOException e) {
				log.error("Cloudinary upload failed: {}", e.getMessage());
				throw new BadRequestException("File upload failed: " + e.getMessage());
			}
		}
		
		private void validateNotEmpty(MultipartFile file) {
			if (file == null || file.isEmpty()) {
				throw new BadRequestException("File cannot be empty");
			}
		}
		
		private void validateSize(MultipartFile file, long maxSize, String fileType) {
			if (file.getSize() > maxSize) {
				long mb = maxSize / (1024 * 1024);
				throw new BadRequestException(fileType + " size must not exceed " + mb + "MB");
			}
		}
		
		private void validateMimeType(MultipartFile file, List<String> allowed, String message) {
			if (!allowed.contains(file.getContentType())) {
				throw new BadRequestException(message);
			}
		}
		
		private String extractExtension(MultipartFile file) {
			String original = file.getOriginalFilename();
			if (original != null && original.contains(".")) {
				return original.substring(original.lastIndexOf('.') + 1);
			}
			return "";
		}
		
		private String detectResourceTypeFromUrl(String url) {
			String lower = url.toLowerCase();
			if (lower.contains("/video/"))  return "video";
			if (lower.contains("/raw/"))    return "raw";
	return "image";
}
}