package com.leedTech.studentFeeOneTimePayment.config.seederConfig;
import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SeederConfig implements ApplicationRunner {

private final UserRepository        userRepository;
private final BCryptPasswordEncoder passwordEncoder;

@Value("${seed.admin.email}")             private String adminEmail;
@Value("${seed.admin.password}")          private String adminPassword;

@Value("${seed.super-admin.email}")       private String superAdminEmail;
@Value("${seed.super-admin.password}")    private String superAdminPassword;

@Value("${seed.principal.email}")         private String principalEmail;
@Value("${seed.principal.password}")      private String principalPassword;

@Value("${seed.vice-principal.email}")    private String vicePrincipalEmail;
@Value("${seed.vice-principal.password}") private String vicePrincipalPassword;

@Value("${seed.school-director.email}")   private String schoolDirectorEmail;
@Value("${seed.school-director.password}")private String schoolDirectorPassword;

@Value("${seed.secretary.email}")         private String secretaryEmail;
@Value("${seed.secretary.password}")      private String secretaryPassword;

@Value("${seed.receptionist.email}")      private String receptionistEmail;
@Value("${seed.receptionist.password}")   private String receptionistPassword;

@Value("${seed.teacher.email}")                private String teacherEmail;
@Value("${seed.teacher.password}")             private String teacherPassword;

@Value("${seed.professor.email}")              private String professorEmail;
@Value("${seed.professor.password}")           private String professorPassword;

@Value("${seed.head-of-department.email}")     private String hodEmail;
@Value("${seed.head-of-department.password}")  private String hodPassword;

@Value("${seed.class-teacher.email}")          private String classTeacherEmail;
@Value("${seed.class-teacher.password}")       private String classTeacherPassword;

@Value("${seed.substitute-teacher.email}")     private String substituteEmail;
@Value("${seed.substitute-teacher.password}")  private String substitutePassword;

@Value("${seed.teaching-assistant.email}")     private String teachingAssistantEmail;
@Value("${seed.teaching-assistant.password}")  private String teachingAssistantPassword;

@Value("${seed.tutor.email}")                  private String tutorEmail;
@Value("${seed.tutor.password}")               private String tutorPassword;

@Value("${seed.counselor.email}")              private String counselorEmail;
@Value("${seed.counselor.password}")           private String counselorPassword;

@Value("${seed.librarian.email}")              private String librarianEmail;
@Value("${seed.librarian.password}")           private String librarianPassword;

@Value("${seed.accountant.email}")        private String accountantEmail;
@Value("${seed.accountant.password}")     private String accountantPassword;

@Value("${seed.cashier.email}")           private String cashierEmail;
@Value("${seed.cashier.password}")        private String cashierPassword;

@Value("${seed.it-staff.email}")          private String itStaffEmail;
@Value("${seed.it-staff.password}")       private String itStaffPassword;

@Value("${seed.security.email}")          private String securityEmail;
@Value("${seed.security.password}")       private String securityPassword;

@Value("${seed.nurse.email}")             private String nurseEmail;
@Value("${seed.nurse.password}")          private String nursePassword;

@Value("${seed.janitor.email}")           private String janitorEmail;
@Value("${seed.janitor.password}")        private String janitorPassword;

@Value("${seed.driver.email}")            private String driverEmail;
@Value("${seed.driver.password}")         private String driverPassword;

@Value("${seed.canteen-staff.email}")     private String canteenStaffEmail;
@Value("${seed.canteen-staff.password}")  private String canteenStaffPassword;

@Value("${seed.auditor.email}")           private String auditorEmail;
@Value("${seed.auditor.password}")        private String auditorPassword;

@Value("${seed.examiner.email}")          private String examinerEmail;
@Value("${seed.examiner.password}")       private String examinerPassword;

@Value("${seed.guest.email}")             private String guestEmail;
@Value("${seed.guest.password}")          private String guestPassword;

@Override
public void run(ApplicationArguments args) {
	log.info("Running database seeder...");
	
	List<SeedUser> seedUsers = List.of(
			// Management
			new SeedUser("Admin",             "LeedTech",   adminEmail,             adminPassword,             UserRole.ADMIN),
			new SeedUser("Super",             "Admin",      superAdminEmail,         superAdminPassword,        UserRole.SUPER_ADMIN),
			new SeedUser("John",              "Principal",  principalEmail,          principalPassword,         UserRole.PRINCIPAL),
			new SeedUser("Mary",              "Vice",       vicePrincipalEmail,      vicePrincipalPassword,     UserRole.VICE_PRINCIPAL),
			new SeedUser("James",             "Director",   schoolDirectorEmail,     schoolDirectorPassword,    UserRole.SCHOOL_DIRECTOR),
			new SeedUser("Grace",             "Secretary",  secretaryEmail,          secretaryPassword,         UserRole.SECRETARY),
			new SeedUser("Clara",             "Reception",  receptionistEmail,       receptionistPassword,      UserRole.RECEPTIONIST),
			
			new SeedUser("Paul",              "Teacher",    teacherEmail,            teacherPassword,           UserRole.TEACHER),
			new SeedUser("David",             "Professor",  professorEmail,          professorPassword,         UserRole.PROFESSOR),
			new SeedUser("Samuel",            "HoD",        hodEmail,                hodPassword,               UserRole.HEAD_OF_DEPARTMENT),
			new SeedUser("Ruth",              "Class",      classTeacherEmail,       classTeacherPassword,      UserRole.CLASS_TEACHER),
			new SeedUser("Peter",             "Substitute", substituteEmail,         substitutePassword,        UserRole.SUBSTITUTE_TEACHER),
			new SeedUser("Esther",            "Assistant",  teachingAssistantEmail,  teachingAssistantPassword, UserRole.TEACHING_ASSISTANT),
			new SeedUser("Daniel",            "Tutor",      tutorEmail,              tutorPassword,             UserRole.TUTOR),
			new SeedUser("Naomi",             "Counselor",  counselorEmail,          counselorPassword,         UserRole.COUNSELOR),
			new SeedUser("Deborah",           "Librarian",  librarianEmail,          librarianPassword,         UserRole.LIBRARIAN),
			
			new SeedUser("Joseph",            "Accountant", accountantEmail,         accountantPassword,        UserRole.ACCOUNTANT),
			new SeedUser("Rachel",            "Cashier",    cashierEmail,            cashierPassword,           UserRole.CASHIER),
			new SeedUser("Nathan",            "ItStaff",    itStaffEmail,            itStaffPassword,           UserRole.IT_STAFF),
			new SeedUser("Moses",             "Security",   securityEmail,           securityPassword,          UserRole.SECURITY),
			new SeedUser("Abigail",           "Nurse",      nurseEmail,              nursePassword,             UserRole.NURSE),
			new SeedUser("Caleb",             "Janitor",    janitorEmail,            janitorPassword,           UserRole.JANITOR),
			new SeedUser("Joshua",            "Driver",     driverEmail,             driverPassword,            UserRole.DRIVER),
			new SeedUser("Lydia",             "Canteen",    canteenStaffEmail,       canteenStaffPassword,      UserRole.CANTEEN_STAFF),
			
			new SeedUser("Aaron",             "Auditor",    auditorEmail,            auditorPassword,           UserRole.AUDITOR),
			new SeedUser("Solomon",           "Examiner",   examinerEmail,           examinerPassword,          UserRole.EXAMINER),
			new SeedUser("Guest",             "User",       guestEmail,              guestPassword,             UserRole.GUEST)
	);
	
	int seeded = 0;
	for (SeedUser seedUser : seedUsers) {
		if (!userRepository.existsByEmail(seedUser.email())) {
			User user = new User();
			user.setFirstName(seedUser.firstName());
			user.setLastName(seedUser.lastName());
			user.setEmail(seedUser.email());
			user.setPassword(passwordEncoder.encode(seedUser.password()));
			user.setRole(seedUser.role());
			user.setAccountVerified(true);
			user.setAccountActive(true);
			user.setAccountBlocked(false);
			user.setFailedLoginAttempts(0);
			userRepository.save(user);
			seeded++;
			log.info("Seeded user: {} - {}", seedUser.role(), seedUser.email());
		} else {
			log.debug("User already exists, skipping: {}", seedUser.email());
		}
	}
	
	log.info("Seeder completed. {} new user(s) seeded.", seeded);
}

private record SeedUser(
		String   firstName,
		String   lastName,
		String   email,
		String   password,
		UserRole role
) {}
}