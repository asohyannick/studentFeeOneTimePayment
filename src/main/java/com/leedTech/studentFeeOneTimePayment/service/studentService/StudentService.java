package com.leedTech.studentFeeOneTimePayment.service.studentService;

import com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus;
import com.leedTech.studentFeeOneTimePayment.constant.Gender;
import com.leedTech.studentFeeOneTimePayment.dto.studentProfile.StudentProfileRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.studentProfile.StudentProfileResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.mapper.studentProfileMapper.StudentProfileMapper;
import com.leedTech.studentFeeOneTimePayment.repository.studentProfileRepository.StudentProfileRepository;
import com.leedTech.studentFeeOneTimePayment.repository.userRepository.UserRepository;
import com.leedTech.studentFeeOneTimePayment.utils.specification.StudentProfileSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

private final StudentProfileMapper      studentProfileMapper;
private final StudentProfileRepository  studentProfileRepository;
private final UserRepository            userRepository;
private final ProfilePictureService     profilePictureService;

private StudentProfile findProfileById(UUID studentProfileId) {
	return studentProfileRepository.findById(studentProfileId)
			       .filter(p -> !p.isDeleted())
			       .orElseThrow(() -> new NotFoundException(
					       "Student profile not found with id: " + studentProfileId
			       ));
}

@Transactional(readOnly = true)
public StudentProfileResponseDto createStudentProfile(StudentProfileRequestDto request) {
	
	User student = userRepository.findById(request.getStudentId())
			               .orElseThrow(() -> new NotFoundException(
					               "User not found with id: " + request.getStudentId()
			               ));
	
	if (studentProfileRepository.existsByStudentId(request.getStudentId())) {
		throw new BadRequestException(
				"Student profile already exists for user: " + request.getStudentId()
		);
	}
	
	if (request.getStudentNumber() != null &&
			    studentProfileRepository.existsByStudentNumber(request.getStudentNumber().trim ())) {
		throw new BadRequestException("Student number already in use: " + request.getStudentNumber());
	}
	if (request.getNationalId() != null &&
			    studentProfileRepository.existsByNationalId(request.getNationalId())) {
		throw new BadRequestException("National ID already in use: " + request.getNationalId());
	}
	if (request.getPersonalEmail() != null &&
			    studentProfileRepository.existsByPersonalEmail(request.getPersonalEmail())) {
		throw new BadRequestException("Personal email already in use: " + request.getPersonalEmail());
	}
	
	StudentProfile profile = studentProfileMapper.toEntity(request);
	profile.setStudent(student);
	StudentProfile saved = studentProfileRepository.save(profile);
	
	MultipartFile profilePicture = request.getProfilePicture();
	if (profilePicture != null && !profilePicture.isEmpty()) {
		String imageUrl = profilePictureService.uploadProfilePicture(saved.getId(), profilePicture);
		saved.setProfilePictureUrl(imageUrl);
		saved = studentProfileRepository.save(saved);
	}
	
	log.info("Student profile created for: {}", student.getEmail());
	return studentProfileMapper.toResponseDto(saved);
}


@Transactional(readOnly = true)
public Page<StudentProfileResponseDto> fetchAllStudents(
		int page,
		int size,
		String sortBy,
		String sortDir
) {
	Sort sort = sortDir.equalsIgnoreCase("asc")
			            ? Sort.by(sortBy).ascending()
			            : Sort.by(sortBy).descending();
	
	Pageable pageable = PageRequest.of(page, size, sort);
	
	Specification<StudentProfile> spec = Specification
			                                     .allOf(StudentProfileSpecification.isNotDeleted());
	
	return studentProfileRepository.findAll(spec, pageable)
			       .map(studentProfileMapper::toSummaryDto);
}

@Transactional(readOnly = true)
public StudentProfileResponseDto fetchStudentById(UUID studentProfileId) {
	StudentProfile profile = findProfileById(studentProfileId);
	return studentProfileMapper.toResponseDto(profile);
}

@Transactional
public StudentProfileResponseDto updateStudentProfile(
		UUID studentProfileId,
		StudentProfileRequestDto request
) {
	StudentProfile profile = findProfileById(studentProfileId);
	
	if (request.getStudentNumber() != null) {
		profile.setStudentNumber(request.getStudentNumber().trim());
	}
	if (request.getStudentNumber() != null &&
			    !request.getStudentNumber().equals(profile.getStudentNumber()) &&
			    studentProfileRepository.existsByStudentNumber(request.getStudentNumber())) {
		throw new BadRequestException("Student number already in use: " + request.getStudentNumber());
	}
	if (request.getNationalId() != null &&
			    !request.getNationalId().equals(profile.getNationalId()) &&
			    studentProfileRepository.existsByNationalId(request.getNationalId())) {
		throw new BadRequestException("National ID already in use: " + request.getNationalId());
	}
	if (request.getPersonalEmail() != null &&
			    !request.getPersonalEmail().equals(profile.getPersonalEmail()) &&
			    studentProfileRepository.existsByPersonalEmail(request.getPersonalEmail())) {
		throw new BadRequestException("Personal email already in use: " + request.getPersonalEmail());
	}
	
	studentProfileMapper.updateEntityFromDto(request, profile);
	
	MultipartFile profilePicture = request.getProfilePicture();
	if (profilePicture != null && !profilePicture.isEmpty()) {
		String imageUrl = profilePictureService.uploadProfilePicture(
				studentProfileId, profilePicture
		);
		profile.setProfilePictureUrl(imageUrl);
	}
	
	StudentProfile updated = studentProfileRepository.save(profile);
	log.info("Student profile updated: {}", studentProfileId);
	return studentProfileMapper.toResponseDto(updated);
}

@Transactional(readOnly = true)
public void deleteStudentProfile(UUID studentProfileId) {
	StudentProfile profile = findProfileById(studentProfileId);
	
	if (profile.getProfilePictureUrl() != null) {
		profilePictureService.deleteProfilePicture(studentProfileId);
	}
	
	profile.setDeleted(true);
	profile.setDeletedAt(Instant.now());
	profile.setEnrollmentStatus(EnrollmentStatus.INACTIVE);
	studentProfileRepository.save(profile);
	
	log.info("Student profile soft deleted: {}", studentProfileId);
}

@Transactional(readOnly = true)
public long countAllStudents() {
	return studentProfileRepository.countByEnrollmentStatus(EnrollmentStatus.ACTIVE);
}

@Transactional(readOnly = true)
public Page<StudentProfileResponseDto> searchAndFilterStudents(
		String keyword,
		String currentClass,
		String academicYear,
		String currentSection,
		EnrollmentStatus enrollmentStatus,
		Gender gender,
		String nationality,
		String city,
		String country,
		Boolean isFeeDefaulter,
		Boolean isBoarder,
		Boolean usesSchoolTransport,
		String hostelName,
		String transportRoute,
		Double minScholarshipPercentage,
		int page,
		int size,
		String sortBy,
		String sortDir
) {
	Sort sort = sortDir.equalsIgnoreCase("asc")
			            ? Sort.by(sortBy).ascending()
			            : Sort.by(sortBy).descending();
	
	Pageable pageable = PageRequest.of(page, size, sort);
	
	Specification <StudentProfile> spec = Specification
			                                     .allOf (StudentProfileSpecification.isNotDeleted())
			                                     .and( StudentProfileSpecification.hasKeyword(keyword))
			                                     .and(StudentProfileSpecification.hasCurrentClass(currentClass))
			                                     .and(StudentProfileSpecification.hasAcademicYear(academicYear))
			                                     .and(StudentProfileSpecification.hasCurrentSection(currentSection))
			                                     .and(StudentProfileSpecification.hasEnrollmentStatus(enrollmentStatus))
			                                     .and(StudentProfileSpecification.hasGender(gender))
			                                     .and(StudentProfileSpecification.hasNationality(nationality))
			                                     .and(StudentProfileSpecification.hasCity(city))
			                                     .and(StudentProfileSpecification.hasCountry(country))
			                                     .and(StudentProfileSpecification.isFeeDefaulter(isFeeDefaulter))
			                                     .and(StudentProfileSpecification.isBoarder(isBoarder))
			                                     .and(StudentProfileSpecification.usesSchoolTransport(usesSchoolTransport))
			                                     .and(StudentProfileSpecification.hasHostelName(hostelName))
			                                     .and(StudentProfileSpecification.hasTransportRoute(transportRoute))
			                                     .and(StudentProfileSpecification.hasMinScholarship(minScholarshipPercentage));
	
	return studentProfileRepository.findAll(spec, pageable)
			       .map(studentProfileMapper::toSummaryDto);
}

@Transactional(readOnly = true)
public List<StudentProfileResponseDto> fetchFeeDefaulters(String academicYear) {
	return studentProfileRepository.findFeeDefaultersByAcademicYear(academicYear)
			       .stream()
			       .map(studentProfileMapper::toSummaryDto)
			       .toList();
}

@Transactional(readOnly = true)
public List<StudentProfileResponseDto> fetchBoardersByHostel(String hostelName) {
	return studentProfileRepository.findBoardersByHostel(hostelName)
			       .stream()
			       .map(studentProfileMapper::toSummaryDto)
			       .toList();
}

@Transactional(readOnly = true)
public List<StudentProfileResponseDto> fetchStudentsWithScholarship(Double minPercentage) {
	return studentProfileRepository.findStudentsWithScholarship(minPercentage)
			       .stream()
			       .map(studentProfileMapper::toSummaryDto)
			       .toList();
}

}
