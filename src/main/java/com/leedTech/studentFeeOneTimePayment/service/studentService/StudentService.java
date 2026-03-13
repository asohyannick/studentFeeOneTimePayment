package com.leedTech.studentFeeOneTimePayment.service.studentService;
import com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus;
import com.leedTech.studentFeeOneTimePayment.constant.Gender;
import com.leedTech.studentFeeOneTimePayment.dto.studentProfile.StudentProfileRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.studentProfile.StudentProfileResponseDto;
import com.leedTech.studentFeeOneTimePayment.dto.studentProfile.StudentProfileSummaryDto;
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


@Transactional
public StudentProfileResponseDto createStudentProfile(StudentProfileRequestDto request, MultipartFile profilePicture) {
	
	User student = userRepository.findById(request.studentId())
			               .orElseThrow(() -> new NotFoundException(
					               "User not found with id: " + request.studentId()
			               ));
	
	if (studentProfileRepository.existsByStudentId(request.studentId())) {
		throw new BadRequestException(
				"Student profile already exists for user: " + request.studentId()
		);
	}
	
	if (request.studentNumber() != null &&
			    studentProfileRepository.existsByStudentNumber(request.studentNumber())) {
		throw new BadRequestException("Student number already in use: " + request.studentNumber());
	}
	if (request.nationalId() != null &&
			    studentProfileRepository.existsByNationalId(request.nationalId())) {
		throw new BadRequestException("National ID already in use: " + request.nationalId());
	}
	if (request.personalEmail() != null &&
			    studentProfileRepository.existsByPersonalEmail(request.personalEmail())) {
		throw new BadRequestException("Personal email already in use: " + request.personalEmail());
	}
	
	StudentProfile profile = studentProfileMapper.toEntity(request);
	profile.setStudent(student);
	StudentProfile saved = studentProfileRepository.save(profile);
	
	if (profilePicture != null && !profilePicture.isEmpty()) {
		String imageUrl = profilePictureService.uploadProfilePicture(saved.getId(), profilePicture);
		saved.setProfilePictureUrl(imageUrl);
		saved = studentProfileRepository.save(saved);
	}
	
	log.info("Student profile created for: {}", student.getEmail());
	return studentProfileMapper.toResponseDto(saved);
}

public Page<StudentProfileSummaryDto> fetchAllStudents(
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

public StudentProfileResponseDto fetchStudentById(UUID studentProfileId) {
	StudentProfile profile = findProfileById(studentProfileId);
	return studentProfileMapper.toResponseDto(profile);
}

public StudentProfileResponseDto fetchStudentByStudentNumber(String studentNumber) {
	StudentProfile profile = studentProfileRepository.findByStudentNumber(studentNumber)
			                         .orElseThrow(() -> new NotFoundException(
					                         "Student profile not found with student number: " + studentNumber
			                         ));
	return studentProfileMapper.toResponseDto(profile);
}

@Transactional
public StudentProfileResponseDto updateStudentProfile(
		UUID studentProfileId,
		StudentProfileRequestDto request,
		MultipartFile profilePicture
) {
	StudentProfile profile = findProfileById(studentProfileId);
	
	if (request.studentNumber() != null &&
			    !request.studentNumber().equals(profile.getStudentNumber()) &&
			    studentProfileRepository.existsByStudentNumber(request.studentNumber())) {
		throw new BadRequestException("Student number already in use: " + request.studentNumber());
	}
	if (request.nationalId() != null &&
			    !request.nationalId().equals(profile.getNationalId()) &&
			    studentProfileRepository.existsByNationalId(request.nationalId())) {
		throw new BadRequestException("National ID already in use: " + request.nationalId());
	}
	if (request.personalEmail() != null &&
			    !request.personalEmail().equals(profile.getPersonalEmail()) &&
			    studentProfileRepository.existsByPersonalEmail(request.personalEmail())) {
		throw new BadRequestException("Personal email already in use: " + request.personalEmail());
	}
	
	studentProfileMapper.updateEntityFromDto(request, profile);
	
	if (profilePicture != null && !profilePicture.isEmpty()) {
		String imageUrl = profilePictureService.uploadProfilePicture(studentProfileId, profilePicture);
		profile.setProfilePictureUrl(imageUrl);
	}
	
	StudentProfile updated = studentProfileRepository.save(profile);
	log.info("Student profile updated: {}", studentProfileId);
	return studentProfileMapper.toResponseDto(updated);
}

@Transactional
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

public long countAllStudents() {
	return studentProfileRepository.countByEnrollmentStatus(EnrollmentStatus.ACTIVE);
}

public long countByClassAndYear(String currentClass, String academicYear) {
	return studentProfileRepository.countByClassAndYear(currentClass, academicYear);
}

public Page<StudentProfileSummaryDto> searchAndFilterStudents(
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

public List<StudentProfileSummaryDto> fetchFeeDefaulters(String academicYear) {
	return studentProfileRepository.findFeeDefaultersByAcademicYear(academicYear)
			       .stream()
			       .map(studentProfileMapper::toSummaryDto)
			       .toList();
}

public List<StudentProfileSummaryDto> fetchBoardersByHostel(String hostelName) {
	return studentProfileRepository.findBoardersByHostel(hostelName)
			       .stream()
			       .map(studentProfileMapper::toSummaryDto)
			       .toList();
}

public List<StudentProfileSummaryDto> fetchStudentsWithScholarship(Double minPercentage) {
	return studentProfileRepository.findStudentsWithScholarship(minPercentage)
			       .stream()
			       .map(studentProfileMapper::toSummaryDto)
			       .toList();
}

}
