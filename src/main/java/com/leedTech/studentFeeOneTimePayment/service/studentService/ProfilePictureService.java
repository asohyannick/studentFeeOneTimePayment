package com.leedTech.studentFeeOneTimePayment.service.studentService;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.repository.studentProfileRepository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilePictureService {

private final Cloudinary                cloudinary;
private final StudentProfileRepository studentProfileRepository;

private static final long   MAX_FILE_SIZE   = 5 * 1024 * 1024; // 5MB
private static final List<String> ALLOWED_TYPES = Arrays.asList(
		"image/jpeg", "image/png", "image/webp", "image/jpg"
);

@Transactional
public String uploadProfilePicture(UUID studentProfileId, MultipartFile file) {
	validateFile(file);
	
	StudentProfile profile = studentProfileRepository.findById(studentProfileId)
			                         .orElseThrow(() -> new NotFoundException (
					                         "Student profile not found with id: " + studentProfileId
			                         ));
	
	try {
		if (profile.getProfilePictureUrl() != null) {
			deleteFromCloudinary(profile.getProfilePictureUrl());
		}
		
		Map<?, ?> uploadResult = cloudinary.uploader().upload(
				file.getBytes(),
				ObjectUtils.asMap(
						"folder",          "student-fee-payment/profile-pictures",
						"public_id",       "student_" + studentProfileId,
						"overwrite",       true,
						"transformation",  "c_fill,w_400,h_400,g_face,q_auto,f_auto",
						"resource_type",   "image"
				)
		);
		
		String imageUrl = (String) uploadResult.get("secure_url");
		profile.setProfilePictureUrl(imageUrl);
		studentProfileRepository.save(profile);
		
		log.info("Profile picture uploaded for student profile: {}", studentProfileId);
		return imageUrl;
		
	} catch (IOException e) {
		log.error("Failed to upload profile picture: {}", e.getMessage());
		throw new BadRequestException ("Failed to upload profile picture", e);
	}
}


@Transactional
public void deleteProfilePicture(UUID studentProfileId) {
	StudentProfile profile = studentProfileRepository.findById(studentProfileId)
			                         .orElseThrow(() -> new NotFoundException (
					                         "Student profile not found with id: " + studentProfileId
			                         ));
	
	if (profile.getProfilePictureUrl() == null) {
		throw new BadRequestException("No profile picture found to delete");
	}
	
	deleteFromCloudinary(profile.getProfilePictureUrl());
	profile.setProfilePictureUrl(null);
	studentProfileRepository.save(profile);
	
	log.info("Profile picture deleted for student profile: {}", studentProfileId);
}

private void validateFile(MultipartFile file) {
	if (file == null || file.isEmpty()) {
		throw new BadRequestException("Profile picture file cannot be empty");
	}
	if (file.getSize() > MAX_FILE_SIZE) {
		throw new BadRequestException("File size must not exceed 5MB");
	}
	if (!ALLOWED_TYPES.contains(file.getContentType())) {
		throw new BadRequestException("Only JPEG, JPG, PNG and WEBP images are allowed");
	}
}

private void deleteFromCloudinary(String imageUrl) {
	try {
		String publicId = extractPublicId(imageUrl);
		cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
		log.info("Deleted image from Cloudinary: {}", publicId);
	} catch (IOException e) {
		log.warn("Failed to delete old image from Cloudinary: {}", e.getMessage());
	}
}

private String extractPublicId(String imageUrl) {
	String withoutExtension = imageUrl.substring(0, imageUrl.lastIndexOf('.'));
	return withoutExtension.substring(withoutExtension.indexOf("student-fee-payment"));
}
}