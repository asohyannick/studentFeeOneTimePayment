package com.leedTech.studentFeeOneTimePayment.repository.studentProfileRepository;
import com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, UUID>, JpaSpecificationExecutor<StudentProfile> {

		@EntityGraph(attributePaths = {"student"})
		Page<StudentProfile> findAll(Specification<StudentProfile> spec, Pageable pageable);

		@EntityGraph(attributePaths = {"student"})
		@Query("""
		    SELECT sp FROM StudentProfile sp
		    WHERE sp.studentNumber = :studentNumber
		    AND (sp.isDeleted = false OR sp.isDeleted IS NULL)
		    """)
		Optional<StudentProfile> findByStudentNumber(@Param("studentNumber") String studentNumber);
		
		boolean existsByStudentId(UUID studentId);
		boolean existsByStudentNumber(String studentNumber);
		boolean existsByNationalId(String nationalId);
		boolean existsByPersonalEmail(String personalEmail);
		
		@Query("""
		        SELECT sp FROM StudentProfile sp
		        WHERE sp.isDeleted = false
		        AND sp.isFeeDefaulter = true
		        AND sp.academicYear = :academicYear
		        ORDER BY sp.student.lastName ASC
		        """)
		List<StudentProfile> findFeeDefaultersByAcademicYear(@Param("academicYear") String academicYear);
		
		@Query("""
		        SELECT COUNT(sp) FROM StudentProfile sp
		        WHERE sp.isDeleted = false
		        AND sp.enrollmentStatus = :status
		        """)
		long countByEnrollmentStatus(@Param("status") EnrollmentStatus status);
		
		@Query("""
		        SELECT COUNT(sp) FROM StudentProfile sp
		        WHERE sp.isDeleted = false
		        AND sp.currentClass = :currentClass
		        AND sp.academicYear = :academicYear
		        """)
		long countByClassAndYear(
				@Param("currentClass") String currentClass,
				@Param("academicYear") String academicYear
		);
		
		@Query("""
		        SELECT sp FROM StudentProfile sp
		        WHERE sp.isDeleted = false
		        AND sp.isBoarder = true
		        AND sp.hostelName = :hostelName
		        ORDER BY sp.roomNumber ASC
		        """)
		List<StudentProfile> findBoardersByHostel(@Param("hostelName") String hostelName);
		
		@Query("""
		        SELECT sp FROM StudentProfile sp
		        WHERE sp.isDeleted = false
		        AND sp.scholarshipPercentage >= :minPercentage
		        ORDER BY sp.scholarshipPercentage DESC
		        """)
		List<StudentProfile> findStudentsWithScholarship(@Param("minPercentage") Double minPercentage);
		
		@EntityGraph(attributePaths = {"student"})
		@Query("""
		        SELECT sp FROM StudentProfile sp
		        WHERE sp.student.id = :studentId
		        AND sp.isDeleted = false
		        """)
		Optional<StudentProfile> findByStudentIdAndIsDeletedFalse(@Param("studentId") UUID studentId);
		
		@EntityGraph(attributePaths = {"student"})
		@Query("""
		        SELECT sp FROM StudentProfile sp
		        WHERE sp.isDeleted = false
		        AND sp.enrollmentStatus = :status
		        ORDER BY sp.student.lastName ASC
		        """)
		List<StudentProfile> findAllActiveStudents(@Param("status") EnrollmentStatus status);
}