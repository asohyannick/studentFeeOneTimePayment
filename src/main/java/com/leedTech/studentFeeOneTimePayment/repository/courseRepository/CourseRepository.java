package com.leedTech.studentFeeOneTimePayment.repository.courseRepository;

import com.leedTech.studentFeeOneTimePayment.constant.CourseStatus;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>,
		                                          JpaSpecificationExecutor<Course> {
	
	boolean existsByCourseCode(String courseCode);
	boolean existsByCourseName(String courseName);
	
	Optional<Course> findByIdAndDeletedFalse(UUID id);
	Optional<Course> findByCourseCodeAndDeletedFalse(String courseCode);
	
	Page<Course> findAllByDeletedFalse(Pageable pageable);
	
	long countByDeletedFalse();
	long countByDeletedFalseAndActiveTrue();
	long countByDepartmentAndDeletedFalse(String department);
	long countByCourseStatusAndDeletedFalse(CourseStatus courseStatus);
}