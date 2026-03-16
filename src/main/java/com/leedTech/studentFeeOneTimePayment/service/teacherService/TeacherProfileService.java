package com.leedTech.studentFeeOneTimePayment.service.teacherService;

import com.leedTech.studentFeeOneTimePayment.config.cloudinaryConfig.CloudinaryConfig;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import com.leedTech.studentFeeOneTimePayment.dto.teacherProfile.TeacherProfileRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.teacherProfile.TeacherProfileResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.teacherProfile.TeacherProfile;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.mapper.teacherProfileMapper.TeacherProfileMapper;
import com.leedTech.studentFeeOneTimePayment.repository.teacherProfileRepository.TeacherProfileRepository;
import com.leedTech.studentFeeOneTimePayment.repository.userRepository.UserRepository;
import jakarta.persistence.criteria.Predicate;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherProfileService {

			private final CloudinaryConfig           cloudinaryConfig;
			private final TeacherProfileMapper       teacherProfileMapper;
			private final TeacherProfileRepository   teacherProfileRepository;
			private final UserRepository             userRepository;
			
			private TeacherProfile findProfileById(UUID profileId) {
				return teacherProfileRepository.findByIdAndDeletedFalse(profileId)
						       .orElseThrow(() -> new NotFoundException(
								       "Teacher profile not found with id: " + profileId
						       ));
			}
			
			private User resolveTeacher(UUID teacherId) {
				User teacher = userRepository.findById(teacherId)
						               .orElseThrow(() -> new NotFoundException(
								               "User not found with id: " + teacherId
						               ));
				if (teacher.getRole() != UserRole.TEACHER &&
						    teacher.getRole() != UserRole.PROFESSOR &&
						    teacher.getRole() != UserRole.HEAD_OF_DEPARTMENT &&
						    teacher.getRole() != UserRole.CLASS_TEACHER &&
						    teacher.getRole() != UserRole.SUBSTITUTE_TEACHER &&
						    teacher.getRole() != UserRole.TEACHING_ASSISTANT &&
						    teacher.getRole() != UserRole.TUTOR) {
					throw new BadRequestException(
							"User with id: " + teacherId + " does not have a teacher role"
					);
				}
				return teacher;
			}
			
			@Transactional
			public TeacherProfileResponseDto createTeacherProfile(TeacherProfileRequestDto request) {
				
				User teacher = resolveTeacher(request.getTeacherId());
				
				if (teacherProfileRepository.existsByTeacherId(request.getTeacherId())) {
					throw new BadRequestException(
							"Teacher profile already exists for user: " + request.getTeacherId()
					);
				}
				if (request.getEmployeeId() != null &&
						    teacherProfileRepository.existsByEmployeeId(request.getEmployeeId())) {
					throw new BadRequestException(
							"Employee ID already in use: " + request.getEmployeeId()
					);
				}
				if (request.getNationalId() != null &&
						    teacherProfileRepository.existsByNationalId(request.getNationalId())) {
					throw new BadRequestException(
							"National ID already in use: " + request.getNationalId()
					);
				}
				if (request.getPersonalEmail() != null &&
						    teacherProfileRepository.existsByPersonalEmail(request.getPersonalEmail())) {
					throw new BadRequestException(
							"Personal email already in use: " + request.getPersonalEmail()
					);
				}
				
				TeacherProfile profile = teacherProfileMapper.toEntity(request);
				profile.setTeacher(teacher);
				
				MultipartFile pictureFile = request.getProfilePictureFile();
				if (pictureFile != null && !pictureFile.isEmpty()) {
					TeacherProfile saved = teacherProfileRepository.save(profile);
					String imageUrl = cloudinaryConfig.uploadProfilePicture(pictureFile, saved.getId());
					saved.setProfilePictureUrl(imageUrl);
					TeacherProfile completed = teacherProfileRepository.save(saved);
					log.info("Teacher profile created with picture for: {}", teacher.getEmail());
					return teacherProfileMapper.toResponseDto(completed);
				}
				
				if (request.getProfilePictureUrl() != null && !request.getProfilePictureUrl().isBlank()) {
					profile.setProfilePictureUrl(request.getProfilePictureUrl());
				}
				
				TeacherProfile saved = teacherProfileRepository.save(profile);
				log.info("Teacher profile created for: {}", teacher.getEmail());
				return teacherProfileMapper.toResponseDto(saved);
			}
			
			@Transactional(readOnly = true)
			public Page<TeacherProfileResponseDto> fetchAllTeacherProfiles(
					int page, int size, String sortBy, String sortDir
			) {
				Sort sort = sortDir.equalsIgnoreCase("asc")
						            ? Sort.by(sortBy).ascending()
						            : Sort.by(sortBy).descending();
				Pageable pageable = PageRequest.of(page, size, sort);
				return teacherProfileRepository.findAllByDeletedFalse(pageable)
						       .map(teacherProfileMapper::toResponseDto);
			}
			
			@Transactional(readOnly = true)
			public TeacherProfileResponseDto fetchTeacherProfileById(UUID profileId) {
				return teacherProfileMapper.toResponseDto(findProfileById(profileId));
			}
			
			@Transactional(readOnly = true)
			public TeacherProfileResponseDto fetchTeacherProfileByTeacherId(UUID teacherId) {
				TeacherProfile profile = teacherProfileRepository
						                         .findByTeacherIdAndDeletedFalse(teacherId)
						                         .orElseThrow(() -> new NotFoundException(
								                         "Teacher profile not found for teacher id: " + teacherId
						                         ));
				return teacherProfileMapper.toResponseDto(profile);
			}
			
			@Transactional(readOnly = true)
			public TeacherProfileResponseDto fetchTeacherProfileByEmployeeId(String employeeId) {
				TeacherProfile profile = teacherProfileRepository
						                         .findByEmployeeIdAndDeletedFalse(employeeId.trim())
						                         .orElseThrow(() -> new NotFoundException(
								                         "Teacher profile not found with employee id: " + employeeId
						                         ));
				return teacherProfileMapper.toResponseDto(profile);
			}
			
			@Transactional
			public TeacherProfileResponseDto updateTeacherProfile(
					UUID profileId, TeacherProfileRequestDto request
			) {
				TeacherProfile profile = findProfileById(profileId);
				
				if (request.getEmployeeId() != null &&
						    !request.getEmployeeId().equals(profile.getEmployeeId()) &&
						    teacherProfileRepository.existsByEmployeeId(request.getEmployeeId())) {
					throw new BadRequestException(
							"Employee ID already in use: " + request.getEmployeeId()
					);
				}
				if (request.getNationalId() != null &&
						    !request.getNationalId().equals(profile.getNationalId()) &&
						    teacherProfileRepository.existsByNationalId(request.getNationalId())) {
					throw new BadRequestException(
							"National ID already in use: " + request.getNationalId()
					);
				}
				if (request.getPersonalEmail() != null &&
						    !request.getPersonalEmail().equals(profile.getPersonalEmail()) &&
						    teacherProfileRepository.existsByPersonalEmail(request.getPersonalEmail())) {
					throw new BadRequestException(
							"Personal email already in use: " + request.getPersonalEmail()
					);
				}
				
				teacherProfileMapper.updateEntityFromDto(request, profile);
				
				MultipartFile pictureFile = request.getProfilePictureFile();
				if (pictureFile != null && !pictureFile.isEmpty()) {
					if (profile.getProfilePictureUrl() != null) {
						cloudinaryConfig.deleteByUrl(profile.getProfilePictureUrl());
					}
					String imageUrl = cloudinaryConfig.uploadProfilePicture(pictureFile, profileId);
					profile.setProfilePictureUrl(imageUrl);
				} else if (request.getProfilePictureUrl() != null &&
						           !request.getProfilePictureUrl().isBlank()) {
					profile.setProfilePictureUrl(request.getProfilePictureUrl());
				}
				
				TeacherProfile updated = teacherProfileRepository.save(profile);
				log.info("Teacher profile updated: {}", profileId);
				return teacherProfileMapper.toResponseDto(updated);
			}
			
			@Transactional
			public void deleteTeacherProfile(UUID profileId) {
				TeacherProfile profile = findProfileById(profileId);
				
				if (profile.getProfilePictureUrl() != null) {
					cloudinaryConfig.deleteByUrl(profile.getProfilePictureUrl());
				}
				
				teacherProfileRepository.softDeleteById(profileId);
				log.info("Teacher profile soft deleted: {}", profileId);
			}
			
			@Transactional(readOnly = true)
			public long countAllTeacherProfiles() {
				return teacherProfileRepository.countByDeletedFalse();
			}
			
			@Transactional(readOnly = true)
			public long countActiveTeacherProfiles() {
				return teacherProfileRepository.countByDeletedFalseAndActiveTrue();
			}
			
			@Transactional(readOnly = true)
			public long countByDepartment(String department) {
				return teacherProfileRepository.countByDepartmentAndDeletedFalse(department);
			}
			
			@Transactional(readOnly = true)
			public long countByEmploymentStatus(EmploymentStatus status) {
				return teacherProfileRepository.countByEmploymentStatusAndDeletedFalse(status);
			}
			
			@Transactional(readOnly = true)
			public long countOnLeave() {
				return teacherProfileRepository.countByOnLeaveTrueAndDeletedFalse();
			}
			
			@Transactional(readOnly = true)
			public long countVerified() {
				return teacherProfileRepository.countByVerifiedTrueAndDeletedFalse();
			}
			
			@Transactional(readOnly = true)
			public Page<TeacherProfileResponseDto> searchAndFilterTeacherProfiles(
					String keyword,
					String department,
					String faculty,
					String academicYear,
					String specialization,
					String highestQualification,
					String subjectsTaught,
					TeacherType teacherType,
					EmploymentType employmentType,
					EmploymentStatus employmentStatus,
					Gender gender,
					String nationality,
					String city,
					String country,
					Boolean active,
					Boolean verified,
					Boolean blocked,
					Boolean onLeave,
					Integer minYearsOfExperience,
					Integer maxYearsOfExperience,
					Double minSalary,
					Double maxSalary,
					LocalDate joinDateFrom,
					LocalDate joinDateTo,
					int page, int size, String sortBy, String sortDir
			) {
				Sort sort = sortDir.equalsIgnoreCase("asc")
						            ? Sort.by(sortBy).ascending()
						            : Sort.by(sortBy).descending();
				Pageable pageable = PageRequest.of(page, size, sort);
				
				Specification<TeacherProfile> spec = buildSpecification(
						keyword, department, faculty, academicYear, specialization,
						highestQualification, subjectsTaught, teacherType, employmentType,
						employmentStatus, gender, nationality, city, country,
						active, verified, blocked, onLeave,
						minYearsOfExperience, maxYearsOfExperience,
						minSalary, maxSalary, joinDateFrom, joinDateTo
				);
				
				return teacherProfileRepository.findAll(spec, pageable)
						       .map(teacherProfileMapper::toResponseDto);
			}
			
			private Specification<TeacherProfile> buildSpecification(
					String keyword,
					String department,
					String faculty,
					String academicYear,
					String specialization,
					String highestQualification,
					String subjectsTaught,
					TeacherType teacherType,
					EmploymentType employmentType,
					EmploymentStatus employmentStatus,
					Gender gender,
					String nationality,
					String city,
					String country,
					Boolean active,
					Boolean verified,
					Boolean blocked,
					Boolean onLeave,
					Integer minYearsOfExperience,
					Integer maxYearsOfExperience,
					Double minSalary,
					Double maxSalary,
					LocalDate joinDateFrom,
					LocalDate joinDateTo
			) {
				return (root, query, cb) -> {
					List<Predicate> predicates = new ArrayList<>();
					
					predicates.add(cb.isFalse(root.get("deleted")));
					
					if (keyword != null && !keyword.isBlank()) {
						String pattern = "%" + keyword.toLowerCase() + "%";
						predicates.add(cb.or(
								cb.like(cb.lower(root.get("department")),           pattern),
								cb.like(cb.lower(root.get("specialization")),       pattern),
								cb.like(cb.lower(root.get("subjectsTaught")),       pattern),
								cb.like(cb.lower(root.get("highestQualification")), pattern),
								cb.like(cb.lower(root.get("employeeId")),           pattern),
								cb.like(cb.lower(root.get("skills")),               pattern),
								cb.like(cb.lower(root.get("certifications")),       pattern),
								cb.like(cb.lower(root.join("teacher").get("firstName")), pattern),
								cb.like(cb.lower(root.join("teacher").get("lastName")),  pattern),
								cb.like(cb.lower(root.join("teacher").get("email")),     pattern)
						));
					}
					
					if (department        != null && !department.isBlank())
						predicates.add(cb.equal(cb.lower(root.get("department")),        department.trim().toLowerCase()));
					if (faculty           != null && !faculty.isBlank())
						predicates.add(cb.equal(cb.lower(root.get("faculty")),           faculty.trim().toLowerCase()));
					if (academicYear      != null && !academicYear.isBlank())
						predicates.add(cb.equal(root.get("academicYear"),                academicYear.trim()));
					if (specialization    != null && !specialization.isBlank())
						predicates.add(cb.like(cb.lower(root.get("specialization")),     "%" + specialization.trim().toLowerCase() + "%"));
					if (highestQualification != null && !highestQualification.isBlank())
						predicates.add(cb.equal(cb.lower(root.get("highestQualification")), highestQualification.trim().toLowerCase()));
					if (subjectsTaught    != null && !subjectsTaught.isBlank())
						predicates.add(cb.like(cb.lower(root.get("subjectsTaught")),     "%" + subjectsTaught.trim().toLowerCase() + "%"));
					if (nationality       != null && !nationality.isBlank())
						predicates.add(cb.equal(cb.lower(root.get("nationality")),       nationality.trim().toLowerCase()));
					if (city              != null && !city.isBlank())
						predicates.add(cb.equal(cb.lower(root.get("city")),              city.trim().toLowerCase()));
					if (country           != null && !country.isBlank())
						predicates.add(cb.equal(cb.lower(root.get("country")),           country.trim().toLowerCase()));
					
					if (teacherType       != null) predicates.add(cb.equal(root.get("teacherType"),       teacherType));
					if (employmentType    != null) predicates.add(cb.equal(root.get("employmentType"),    employmentType));
					if (employmentStatus  != null) predicates.add(cb.equal(root.get("employmentStatus"),  employmentStatus));
					if (gender            != null) predicates.add(cb.equal(root.get("gender"),            gender));
					
					if (active   != null) predicates.add(cb.equal(root.get("active"),   active));
					if (verified != null) predicates.add(cb.equal(root.get("verified"), verified));
					if (blocked  != null) predicates.add(cb.equal(root.get("blocked"),  blocked));
					if (onLeave  != null) predicates.add(cb.equal(root.get("onLeave"),  onLeave));
					
					if (minYearsOfExperience != null)
						predicates.add(cb.greaterThanOrEqualTo(root.get("yearsOfExperience"), minYearsOfExperience));
					if (maxYearsOfExperience != null)
						predicates.add(cb.lessThanOrEqualTo(root.get("yearsOfExperience"),    maxYearsOfExperience));
					if (minSalary != null)
						predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), minSalary));
					if (maxSalary != null)
						predicates.add(cb.lessThanOrEqualTo(root.get("salary"),    maxSalary));
					
					if (joinDateFrom != null)
						predicates.add(cb.greaterThanOrEqualTo(root.get("joinDate"), joinDateFrom));
					if (joinDateTo != null)
						predicates.add(cb.lessThanOrEqualTo(root.get("joinDate"),    joinDateTo));
					
					return cb.and(predicates.toArray(new Predicate[0]));
				};
			}
}