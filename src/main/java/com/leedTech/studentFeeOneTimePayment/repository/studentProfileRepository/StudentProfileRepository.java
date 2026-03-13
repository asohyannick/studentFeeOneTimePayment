package com.leedTech.studentFeeOneTimePayment.repository.studentProfileRepository;
import com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus;
import com.leedTech.studentFeeOneTimePayment.constant.Gender;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
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

Optional<StudentProfile> findByStudentId(UUID studentId);
Optional<StudentProfile> findByStudentNumber(String studentNumber);
Optional<StudentProfile> findByNationalId(String nationalId);
Optional<StudentProfile> findByPassportNumber(String passportNumber);
Optional<StudentProfile> findByPersonalEmail(String personalEmail);
Optional<StudentProfile> findByBirthCertificateNumber(String birthCertificateNumber);

boolean existsByStudentId(UUID studentId);
boolean existsByStudentNumber(String studentNumber);
boolean existsByNationalId(String nationalId);
boolean existsByPassportNumber(String passportNumber);
boolean existsByPersonalEmail(String personalEmail);

List<StudentProfile> findByCurrentClass(String currentClass);
List<StudentProfile> findByAcademicYear(String academicYear);
List<StudentProfile> findByCurrentClassAndAcademicYear(String currentClass, String academicYear);
List<StudentProfile> findByCurrentClassAndCurrentSection(String currentClass, String currentSection);
List<StudentProfile> findByEnrollmentStatus(EnrollmentStatus enrollmentStatus);

List<StudentProfile> findByGender(Gender gender);
List<StudentProfile> findByNationality(String nationality);
List<StudentProfile> findByCity(String city);
List<StudentProfile> findByCountry(String country);

List<StudentProfile> findByIsFeeDefaulter(boolean isFeeDefaulter);
List<StudentProfile> findByScholarshipNameIsNotNull();

List<StudentProfile> findByUsesSchoolTransport(boolean usesSchoolTransport);
List<StudentProfile> findByIsBoarder(boolean isBoarder);
List<StudentProfile> findByHostelName(String hostelName);
List<StudentProfile> findByTransportRoute(String transportRoute);

List<StudentProfile> findByIsDeletedTrue();
List<StudentProfile> findByEnrollmentStatusAndIsDeletedFalse(EnrollmentStatus status);


@Query("""
            SELECT sp FROM StudentProfile sp
            WHERE sp.isDeleted = false
            AND sp.enrollmentStatus = com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus.ACTIVE
            AND sp.currentClass = :currentClass
            AND sp.academicYear = :academicYear
            ORDER BY sp.student.lastName ASC
            """)
List<StudentProfile> findActiveStudentsByClassAndYear(
		@Param("currentClass") String currentClass,
		@Param("academicYear") String academicYear
);

@Query("""
            SELECT sp FROM StudentProfile sp
            WHERE sp.isDeleted = false
            AND (
                LOWER(sp.student.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(sp.student.lastName)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(sp.student.email)     LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(sp.studentNumber)     LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(sp.nationalId)        LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            ORDER BY sp.student.lastName ASC
            """)
List<StudentProfile> searchStudents(@Param("keyword") String keyword);

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


}