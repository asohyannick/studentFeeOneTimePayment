package com.leedTech.studentFeeOneTimePayment.service.studentService;
import com.leedTech.studentFeeOneTimePayment.config.cloudinaryConfig.CloudinaryConfig;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.repository.studentProfileRepository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilePictureService {

	private final CloudinaryConfig cloudinaryConfig;
	private final StudentProfileRepository studentProfileRepository;
	
	@Transactional
	public String uploadProfilePicture( UUID studentProfileId, MultipartFile file) {
		StudentProfile profile = studentProfileRepository.findById(studentProfileId)
				                         .orElseThrow(() -> new NotFoundException (
						                         "Student profile not found with id: " + studentProfileId
				                         ));
		
		if (profile.getProfilePictureUrl() != null) {
			cloudinaryConfig.deleteByUrl(profile.getProfilePictureUrl());
		}
		
		String imageUrl = cloudinaryConfig.uploadProfilePicture(file, studentProfileId);
		
		profile.setProfilePictureUrl(imageUrl);
		studentProfileRepository.save(profile);
		
		log.info("Profile picture uploaded for student: {}", studentProfileId);
		return imageUrl;
	}
	
	@Transactional
	public void deleteProfilePicture(UUID studentProfileId) {
		StudentProfile profile = studentProfileRepository.findById(studentProfileId)
				                         .orElseThrow(() -> new NotFoundException(
						                         "Student profile not found with id: " + studentProfileId
				                         ));
		
		if (profile.getProfilePictureUrl() == null) {
			throw new BadRequestException ("No profile picture found to delete");
		}
		
		cloudinaryConfig.deleteByUrl(profile.getProfilePictureUrl());
		profile.setProfilePictureUrl(null);
		studentProfileRepository.save(profile);
		
		log.info("Profile picture deleted for student: {}", studentProfileId);
	}
}