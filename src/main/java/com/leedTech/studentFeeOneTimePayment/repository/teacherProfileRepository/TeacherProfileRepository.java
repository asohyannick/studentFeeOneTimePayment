package com.leedTech.studentFeeOneTimePayment.repository.teacherProfileRepository;

import com.leedTech.studentFeeOneTimePayment.constant.EmploymentStatus;
import com.leedTech.studentFeeOneTimePayment.constant.EmploymentType;
import com.leedTech.studentFeeOneTimePayment.constant.TeacherType;
import com.leedTech.studentFeeOneTimePayment.entity.teacherProfile.TeacherProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, UUID>,
		                                                  JpaSpecificationExecutor<TeacherProfile> {

		boolean existsByTeacherId(UUID teacherId);
		boolean existsByEmployeeId(String employeeId);
		boolean existsByNationalId(String nationalId);
		boolean existsByPersonalEmail(String personalEmail);
		
		Optional<TeacherProfile> findByIdAndDeletedFalse(UUID id);
		Optional<TeacherProfile> findByTeacherIdAndDeletedFalse(UUID teacherId);
		Optional<TeacherProfile> findByEmployeeIdAndDeletedFalse(String employeeId);
		Optional<TeacherProfile> findByNationalIdAndDeletedFalse(String nationalId);
		
		Page<TeacherProfile> findAllByDeletedFalse(Pageable pageable);
		Page<TeacherProfile> findAllByDeletedFalseAndActiveTrue(Pageable pageable);
		Page<TeacherProfile> findAllByDepartmentAndDeletedFalse(String department, Pageable pageable);
		Page<TeacherProfile> findAllByFacultyAndDeletedFalse(String faculty, Pageable pageable);
		Page<TeacherProfile> findAllByAcademicYearAndDeletedFalse(String academicYear, Pageable pageable);
		Page<TeacherProfile> findAllByTeacherTypeAndDeletedFalse(TeacherType teacherType, Pageable pageable);
		Page<TeacherProfile> findAllByEmploymentTypeAndDeletedFalse(EmploymentType employmentType, Pageable pageable);
		Page<TeacherProfile> findAllByEmploymentStatusAndDeletedFalse(EmploymentStatus employmentStatus, Pageable pageable);
		Page<TeacherProfile> findAllByVerifiedTrueAndDeletedFalse(Pageable pageable);
		Page<TeacherProfile> findAllByOnLeaveTrueAndDeletedFalse(Pageable pageable);
		Page<TeacherProfile> findAllByBlockedTrueAndDeletedFalse(Pageable pageable);
		
		long countByDeletedFalse();
		long countByDeletedFalseAndActiveTrue();
		long countByDepartmentAndDeletedFalse(String department);
		long countByTeacherTypeAndDeletedFalse(TeacherType teacherType);
		long countByEmploymentStatusAndDeletedFalse(EmploymentStatus employmentStatus);
		long countByVerifiedTrueAndDeletedFalse();
		long countByOnLeaveTrueAndDeletedFalse();
		long countByBlockedTrueAndDeletedFalse();
		
		@Query("""
		            SELECT t FROM TeacherProfile t
		            WHERE t.deleted = false
		            AND (
		                LOWER(t.department)          LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		                LOWER(t.specialization)      LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		                LOWER(t.subjectsTaught)      LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		                LOWER(t.highestQualification) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		                LOWER(t.employeeId)          LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		                LOWER(t.teacher.firstName)   LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		                LOWER(t.teacher.lastName)    LIKE LOWER(CONCAT('%', :keyword, '%')) OR
		                LOWER(t.teacher.email)       LIKE LOWER(CONCAT('%', :keyword, '%'))
		            )
		            """)
		Page<TeacherProfile> searchTeacherProfiles(
				@Param("keyword") String keyword,
				Pageable pageable
		);
		
		@Modifying
		@Query("UPDATE TeacherProfile t SET t.deleted = true, t.deletedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
		void softDeleteById(@Param("id") UUID id);
		
		@Modifying
		@Query("UPDATE TeacherProfile t SET t.blocked = true, t.blockedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
		void blockById(@Param("id") UUID id);
		
		@Modifying
		@Query("UPDATE TeacherProfile t SET t.blocked = false, t.blockedAt = null WHERE t.id = :id")
		void unblockById(@Param("id") UUID id);
		
		@Modifying
		@Query("UPDATE TeacherProfile t SET t.verified = true WHERE t.id = :id")
		void verifyById(@Param("id") UUID id);
		
		@Modifying
		@Query("UPDATE TeacherProfile t SET t.onLeave = true WHERE t.id = :id")
		void setOnLeave(@Param("id") UUID id);
		
		@Modifying
		@Query("UPDATE TeacherProfile t SET t.onLeave = false WHERE t.id = :id")
		void returnFromLeave(@Param("id") UUID id);
		
		@Modifying
		@Query("UPDATE TeacherProfile t SET t.usedLeaveDays = t.usedLeaveDays + :days WHERE t.id = :id")
		void incrementUsedLeaveDays(@Param("id") UUID id, @Param("days") int days);
}