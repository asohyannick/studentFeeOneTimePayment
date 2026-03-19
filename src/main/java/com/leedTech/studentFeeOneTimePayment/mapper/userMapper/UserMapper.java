package com.leedTech.studentFeeOneTimePayment.mapper.userMapper;
import com.leedTech.studentFeeOneTimePayment.dto.auth.LoginResponseDto;
import com.leedTech.studentFeeOneTimePayment.dto.auth.RegistrationRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.auth.RegistrationResponseDto;
import com.leedTech.studentFeeOneTimePayment.dto.auth.StudentResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import org.mapstruct.*;

@Mapper(
		componentModel = "spring",
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

		@Mapping(target = "id", ignore = true)
		@Mapping(target = "password", ignore = true)
		@Mapping(target = "accountVerified", constant = "false")
		@Mapping(target = "accountActive", constant = "false")
		@Mapping(target = "accountBlocked", constant = "false")
		@Mapping(target = "failedLoginAttempts", constant = "0")
		@Mapping (target = "otpCode", ignore = true)
		@Mapping(target = "role",    ignore = true)
		@Mapping (target = "otpExpirationDate", ignore = true)
		@Mapping(target = "magicLinkToken", ignore = true)
		@Mapping(target = "magicLinkExpirationDate", ignore = true)
		@Mapping(target = "lastLoginAt", ignore = true)
		@Mapping(target = "lockedUntil", ignore = true)
		@Mapping(target = "createdAt", ignore = true)
		@Mapping(target = "updatedAt", ignore = true)
		User toEntity(RegistrationRequestDto request);
		
		
		@Mapping(target = "message", constant = "Registration successful. Please check your email to verify your account.")
		RegistrationResponseDto toRegistrationResponseDto(User user);
		
		@Mapping(target = "message", ignore = true)
		LoginResponseDto toLoginResponseDto( User user);
		
		StudentResponseDto toStudentResponseDto( User user);
		
		@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
		@Mapping(target = "id", ignore = true)
		@Mapping(target = "password", ignore = true)
		@Mapping(target = "createdAt", ignore = true)
		@Mapping(target = "updatedAt", ignore = true)
		void updateEntityFromDto(RegistrationRequestDto request, @MappingTarget User user);
}
