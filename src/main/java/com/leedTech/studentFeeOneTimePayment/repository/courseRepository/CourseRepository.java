package com.leedTech.studentFeeOneTimePayment.repository.courseRepository;

import com.leedTech.studentFeeOneTimePayment.constant.CourseStatus;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

	@Query ("""
	    SELECT c FROM Course c
	    WHERE c.active = true
	      AND c.deleted = false
	      AND c.courseStatus IN :statuses
	      AND c.currentEnrollmentCount < c.maxCapacity
	      AND c.academicYear = :academicYear
	    ORDER BY c.mandatory DESC, c.department ASC
	    """)
	List <Course> findRecommendableCourses(
			@Param("academicYear") String academicYear,
			@Param ("statuses") List<CourseStatus> statuses
	);

}