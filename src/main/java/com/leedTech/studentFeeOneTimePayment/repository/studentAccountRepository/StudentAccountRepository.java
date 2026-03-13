package com.leedTech.studentFeeOneTimePayment.repository.studentAccountRepository;
import com.leedTech.studentFeeOneTimePayment.entity.studentAccount.StudentAccount;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface StudentAccountRepository extends JpaRepository<StudentAccount, UUID> {
Optional<StudentAccount> findByStudentProfile(StudentProfile studentProfile);
}