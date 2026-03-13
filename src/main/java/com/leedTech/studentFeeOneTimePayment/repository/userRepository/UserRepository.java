package com.leedTech.studentFeeOneTimePayment.repository.userRepository;

import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository< User, UUID> {
Optional<User> findByEmail(String email);
boolean existsByEmail(String email);
List<User> findAllByRole( UserRole role);
Optional<User> findByMagicLinkToken(String magicLinkToken);
}