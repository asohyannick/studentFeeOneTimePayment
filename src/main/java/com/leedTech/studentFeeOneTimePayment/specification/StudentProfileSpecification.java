package com.leedTech.studentFeeOneTimePayment.specification;
import com.leedTech.studentFeeOneTimePayment.constant.EnrollmentStatus;
import com.leedTech.studentFeeOneTimePayment.constant.Gender;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import org.springframework.data.jpa.domain.Specification;

public class StudentProfileSpecification {

			private StudentProfileSpecification() {}
			
			public static Specification<StudentProfile> isNotDeleted() {
				return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
			}
			
			public static Specification<StudentProfile> hasCurrentClass(String currentClass) {
				return (root, query, cb) -> currentClass == null ? null
						                            : cb.equal(root.get("currentClass"), currentClass);
			}
			
			public static Specification<StudentProfile> hasAcademicYear(String academicYear) {
				return (root, query, cb) -> academicYear == null ? null
						                            : cb.equal(root.get("academicYear"), academicYear);
			}
			
			public static Specification<StudentProfile> hasCurrentSection(String currentSection) {
				return (root, query, cb) -> currentSection == null ? null
						                            : cb.equal(root.get("currentSection"), currentSection);
			}
			
			public static Specification<StudentProfile> hasEnrollmentStatus(EnrollmentStatus status) {
				return (root, query, cb) -> status == null ? null
						                            : cb.equal(root.get("enrollmentStatus"), status);
			}
			
			public static Specification<StudentProfile> hasGender(Gender gender) {
				return (root, query, cb) -> gender == null ? null
						                            : cb.equal(root.get("gender"), gender);
			}
			
			public static Specification<StudentProfile> hasNationality(String nationality) {
				return (root, query, cb) -> nationality == null ? null
						                            : cb.equal(root.get("nationality"), nationality);
			}
			
			public static Specification<StudentProfile> hasCity(String city) {
				return (root, query, cb) -> city == null ? null
						                            : cb.equal(root.get("city"), city);
			}
			
			public static Specification<StudentProfile> hasCountry(String country) {
				return (root, query, cb) -> country == null ? null
						                            : cb.equal(root.get("country"), country);
			}
			
			public static Specification<StudentProfile> isFeeDefaulter(Boolean isFeeDefaulter) {
				return (root, query, cb) -> isFeeDefaulter == null ? null
						                            : cb.equal(root.get("isFeeDefaulter"), isFeeDefaulter);
			}
			
			public static Specification<StudentProfile> isBoarder(Boolean isBoarder) {
				return (root, query, cb) -> isBoarder == null ? null
						                            : cb.equal(root.get("isBoarder"), isBoarder);
			}
			
			public static Specification<StudentProfile> usesSchoolTransport(Boolean usesTransport) {
				return (root, query, cb) -> usesTransport == null ? null
						                            : cb.equal(root.get("usesSchoolTransport"), usesTransport);
			}
			
			public static Specification<StudentProfile> hasHostelName(String hostelName) {
				return (root, query, cb) -> hostelName == null ? null
						                            : cb.equal(root.get("hostelName"), hostelName);
			}
			
			public static Specification<StudentProfile> hasTransportRoute(String transportRoute) {
				return (root, query, cb) -> transportRoute == null ? null
						                            : cb.equal(root.get("transportRoute"), transportRoute);
			}
			
			public static Specification<StudentProfile> hasMinScholarship(Double minPercentage) {
				return (root, query, cb) -> minPercentage == null ? null
						                            : cb.greaterThanOrEqualTo(root.get("scholarshipPercentage"), minPercentage);
			}
			
			public static Specification<StudentProfile> hasKeyword(String keyword) {
				return (root, query, cb) -> {
					if (keyword == null || keyword.isBlank()) return null;
					String pattern = "%" + keyword.toLowerCase() + "%";
					return cb.or(
							cb.like(cb.lower(root.get("student").get("firstName")), pattern),
							cb.like(cb.lower(root.get("student").get("lastName")),  pattern),
							cb.like(cb.lower(root.get("student").get("email")),     pattern),
							cb.like(cb.lower(root.get("studentNumber")),            pattern),
							cb.like(cb.lower(root.get("nationalId")),               pattern)
					);
				};
			}
}