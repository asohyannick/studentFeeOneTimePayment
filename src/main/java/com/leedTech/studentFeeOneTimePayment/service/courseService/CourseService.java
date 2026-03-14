package com.leedTech.studentFeeOneTimePayment.service.courseService;

import com.leedTech.studentFeeOneTimePayment.config.cloudinaryConfig.CloudinaryConfig;
import com.leedTech.studentFeeOneTimePayment.constant.*;
import com.leedTech.studentFeeOneTimePayment.dto.course.CourseRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.course.CourseResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.mapper.courseMapper.CourseMapper;
import com.leedTech.studentFeeOneTimePayment.repository.courseRepository.CourseRepository;
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
public class CourseService {

		private final CourseMapper          courseMapper;
		private final CourseRepository      courseRepository;
		private final UserRepository        userRepository;
		private final CloudinaryConfig      cloudinaryConfig;

		private Course findCourseById(UUID courseId) {
			return courseRepository.findByIdAndDeletedFalse(courseId)
					       .orElseThrow(() -> new NotFoundException(
							       "Course not found with id: " + courseId
					       ));
		}
		
		private User resolveTeacher(UUID teacherId) {
			User teacher = userRepository.findById(teacherId)
					               .orElseThrow(() -> new NotFoundException(
							               "User not found with id: " + teacherId
					               ));
			if (teacher.getRole() != UserRole.TEACHER &&
					    teacher.getRole() != UserRole.PROFESSOR &&
					    teacher.getRole() != UserRole.HEAD_OF_DEPARTMENT) {
				throw new BadRequestException(
						"User with id: " + teacherId + " is not a teacher"
				);
			}
			return teacher;
		}
		
		@Transactional
		public CourseResponseDto createCourse(CourseRequestDto request) {
			
			if (courseRepository.existsByCourseCode(request.getCourseCode().trim())) {
				throw new BadRequestException(
						"Course code already in use: " + request.getCourseCode()
				);
			}
			if (courseRepository.existsByCourseName(request.getCourseName().trim())) {
				throw new BadRequestException(
						"Course name already in use: " + request.getCourseName()
				);
			}
			
			User teacher = resolveTeacher(request.getTeacherId());
			Course course = courseMapper.toEntity(request);
			course.setTeacher(teacher);
			
			MultipartFile imageFile = request.getCourseImageFile();
			if (imageFile != null && !imageFile.isEmpty()) {
				Course saved = courseRepository.save(course);
				String imageUrl = cloudinaryConfig.uploadCourseImage(imageFile, saved.getId());
				saved.setCourseUrl(imageUrl);
				Course completed = courseRepository.save(saved);
				log.info("Course created with image: {}", completed.getCourseCode());
				return courseMapper.toResponseDto(completed);
			}
			
			if (request.getCourseUrl() != null && !request.getCourseUrl().isBlank()) {
				course.setCourseUrl(request.getCourseUrl());
			}
			
			Course saved = courseRepository.save(course);
			log.info("Course created: {}", saved.getCourseCode());
			return courseMapper.toResponseDto(saved);
		}
		
		@Transactional(readOnly = true)
		public CourseResponseDto fetchCourseById(UUID courseId) {
			return courseMapper.toResponseDto(findCourseById(courseId));
		}
		
		@Transactional
		public CourseResponseDto updateCourse(UUID courseId, CourseRequestDto request) {
			Course course = findCourseById(courseId);
			
			if (request.getCourseCode() != null &&
					    !request.getCourseCode().trim().equals(course.getCourseCode()) &&
					    courseRepository.existsByCourseCode(request.getCourseCode().trim())) {
				throw new BadRequestException(
						"Course code already in use: " + request.getCourseCode()
				);
			}
			
			if (request.getCourseName() != null &&
					    !request.getCourseName().trim().equals(course.getCourseName()) &&
					    courseRepository.existsByCourseName(request.getCourseName().trim())) {
				throw new BadRequestException(
						"Course name already in use: " + request.getCourseName()
				);
			}
			
			if (request.getTeacherId() != null) {
				course.setTeacher(resolveTeacher(request.getTeacherId()));
			}
			
			courseMapper.updateEntityFromDto(request, course);
			
			MultipartFile imageFile = request.getCourseImageFile();
			if (imageFile != null && !imageFile.isEmpty()) {
				if (course.getCourseUrl() != null) {
					cloudinaryConfig.deleteByUrl(course.getCourseUrl());
				}
				String imageUrl = cloudinaryConfig.uploadCourseImage(imageFile, courseId);
				course.setCourseUrl(imageUrl);
			} else if (request.getCourseUrl() != null && !request.getCourseUrl().isBlank()) {
				course.setCourseUrl(request.getCourseUrl());
			}
			
			Course updated = courseRepository.save(course);
			log.info("Course updated: {}", courseId);
			return courseMapper.toResponseDto(updated);
		}

		@Transactional
		public void deleteCourse(UUID courseId) {
			Course course = findCourseById(courseId);
			
			if (course.getCourseUrl() != null && !course.getCourseUrl().isBlank()) {
				cloudinaryConfig.deleteByUrl(course.getCourseUrl());
			}
			courseRepository.deleteById(courseId);
			log.info("Course permanently deleted: {}", courseId);
		}
		

		@Transactional(readOnly = true)
		public Page<CourseResponseDto> fetchAllCourses(
				int page, int size, String sortBy, String sortDir
		) {
			Sort sort = sortDir.equalsIgnoreCase("asc")
					            ? Sort.by(sortBy).ascending()
					            : Sort.by(sortBy).descending();
			Pageable pageable = PageRequest.of(page, size, sort);
			return courseRepository.findAllByDeletedFalse(pageable)
					       .map(courseMapper::toResponseDto);
		}
		
		@Transactional(readOnly = true)
		public CourseResponseDto fetchCourseByCourseCode(String courseCode) {
			Course course = courseRepository.findByCourseCodeAndDeletedFalse(courseCode.trim())
					                .orElseThrow(() -> new NotFoundException(
							                "Course not found with code: " + courseCode
					                ));
			return courseMapper.toResponseDto(course);
		}
		
		@Transactional(readOnly = true)
		public long countAllCourses() {
			return courseRepository.countByDeletedFalse();
		}
		
		@Transactional(readOnly = true)
		public long countActiveCourses() {
			return courseRepository.countByDeletedFalseAndActiveTrue();
		}
		
		@Transactional(readOnly = true)
		public long countCoursesByDepartment(String department) {
			return courseRepository.countByDepartmentAndDeletedFalse(department);
		}
		
		@Transactional(readOnly = true)
		public long countCoursesByStatus(CourseStatus courseStatus) {
			return courseRepository.countByCourseStatusAndDeletedFalse(courseStatus);
}
		
		@Transactional(readOnly = true)
		public Page<CourseResponseDto> searchAndFilterCourses(
				String keyword,
				String department,
				String faculty,
				String academicYear,
				String semester,
				String courseLevel,
				CourseStatus courseStatus,
				GradeLevelStatus gradeLevel,
				ExamType examType,
				ScheduleType scheduleType,
				ScheduleDayType scheduleDay,
				String instructorName,
				Boolean isActive,
				Boolean isMandatory,
				Boolean isElective,
				Boolean allowLateEnrollment,
				LocalDate startDateFrom,
				LocalDate startDateTo,
				Integer minCreditHours,
				Integer maxCreditHours,
				Double minPassMark,
				Double maxPassMark,
				Integer minCapacity,
				Integer maxCapacity,
				int page,
				int size,
				String sortBy,
				String sortDir
		) {
			Sort sort = sortDir.equalsIgnoreCase("asc")
					            ? Sort.by(sortBy).ascending()
					            : Sort.by(sortBy).descending();
			Pageable pageable = PageRequest.of(page, size, sort);
			
			Specification<Course> spec = buildSpecification(
					keyword, department, faculty, academicYear, semester, courseLevel,
					courseStatus, gradeLevel, examType, scheduleType, scheduleDay,
					instructorName, isActive, isMandatory, isElective, allowLateEnrollment,
					startDateFrom, startDateTo, minCreditHours, maxCreditHours,
					minPassMark, maxPassMark, minCapacity, maxCapacity
			);
			
			return courseRepository.findAll(spec, pageable)
					       .map(courseMapper::toResponseDto);
		}
		
		private Specification<Course> buildSpecification(
				String keyword,
				String department,
				String faculty,
				String academicYear,
				String semester,
				String courseLevel,
				CourseStatus courseStatus,
				GradeLevelStatus gradeLevel,
				ExamType examType,
				ScheduleType scheduleType,
				ScheduleDayType scheduleDay,
				String instructorName,
				Boolean isActive,
				Boolean isMandatory,
				Boolean isElective,
				Boolean allowLateEnrollment,
				LocalDate startDateFrom,
				LocalDate startDateTo,
				Integer minCreditHours,
				Integer maxCreditHours,
				Double minPassMark,
				Double maxPassMark,
				Integer minCapacity,
				Integer maxCapacity
		) {
			return (root, query, cb) -> {
				List<Predicate> predicates = new ArrayList<>();
				
				predicates.add(cb.isFalse(root.get("deleted")));
				
				log.info("Building specification — keyword: {}, department: {}, isActive: {}",
						keyword, department, isActive);
				
				if (keyword != null && !keyword.isBlank()) {
					String pattern = "%" + keyword.toLowerCase() + "%";
					log.info("Applying keyword filter with pattern: {}", pattern);
					predicates.add(cb.or(
							cb.like(cb.lower(root.get("courseName")),     pattern),
							cb.like(cb.lower(root.get("courseCode")),     pattern),
							cb.like(cb.lower(root.get("department")),     pattern),
							cb.like(cb.lower(root.get("description")),    pattern),
							cb.like(cb.lower(root.get("instructorName")), pattern),
							cb.like(cb.lower(root.get("faculty")),        pattern),
							cb.like(cb.lower(root.get("semester")),       pattern),
							cb.like(cb.lower(root.get("courseLevel")),    pattern),
							cb.like(cb.lower(root.get("academicYear")),   pattern)
					));
				}
				
				if (department     != null) predicates.add(cb.equal(cb.lower(root.get("department")),     department.toLowerCase()));
				if (faculty        != null) predicates.add(cb.equal(cb.lower(root.get("faculty")),        faculty.toLowerCase()));
				if (academicYear   != null) predicates.add(cb.equal(root.get("academicYear"),             academicYear));
				if (semester       != null) predicates.add(cb.equal(cb.lower(root.get("semester")),       semester.toLowerCase()));
				if (courseLevel    != null) predicates.add(cb.equal(cb.lower(root.get("courseLevel")),    courseLevel.toLowerCase()));
				if (instructorName != null) predicates.add(cb.like(cb.lower(root.get("instructorName")), "%" + instructorName.toLowerCase() + "%"));
				if (courseStatus   != null) predicates.add(cb.equal(root.get("courseStatus"),             courseStatus));
				if (gradeLevel     != null) predicates.add(cb.equal(root.get("gradeLevel"),               gradeLevel));
				if (examType       != null) predicates.add(cb.equal(root.get("examType"),                 examType));
				if (scheduleType   != null) predicates.add(cb.equal(root.get("scheduleType"),             scheduleType));
				if (scheduleDay    != null) predicates.add(cb.equal(root.get("scheduleDay"),              scheduleDay));
				
				if (isActive           != null) predicates.add(cb.equal(root.get("active"),    isActive));
				if (isMandatory        != null) predicates.add(cb.equal(root.get("mandatory"), isMandatory));
				if (isElective         != null) predicates.add(cb.equal(root.get("elective"),  isElective));
				if (allowLateEnrollment != null) predicates.add(cb.equal(root.get("allowLateEnrollment"), allowLateEnrollment));
				
		
				if (startDateFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), startDateFrom));
				if (startDateTo   != null) predicates.add(cb.lessThanOrEqualTo(root.get("startDate"),    startDateTo));
				
		
				if (minCreditHours != null) predicates.add(cb.greaterThanOrEqualTo(root.get("creditHours"), minCreditHours));
				if (maxCreditHours != null) predicates.add(cb.lessThanOrEqualTo(root.get("creditHours"),    maxCreditHours));
				if (minPassMark    != null) predicates.add(cb.greaterThanOrEqualTo(root.get("passMark"),    minPassMark));
				if (maxPassMark    != null) predicates.add(cb.lessThanOrEqualTo(root.get("passMark"),       maxPassMark));
				if (minCapacity    != null) predicates.add(cb.greaterThanOrEqualTo(root.get("maxCapacity"), minCapacity));
				if (maxCapacity    != null) predicates.add(cb.lessThanOrEqualTo(root.get("maxCapacity"),    maxCapacity));
				
				log.info("Total predicates built: {}", predicates.size());
				return cb.and(predicates.toArray(new Predicate[0]));
			};
		}
}