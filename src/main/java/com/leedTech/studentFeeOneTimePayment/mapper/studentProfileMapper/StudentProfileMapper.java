package com.leedTech.studentFeeOneTimePayment.mapper.studentProfileMapper;

import com.leedTech.studentFeeOneTimePayment.dto.studentProfile.StudentProfileRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.studentProfile.StudentProfileResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import org.mapstruct.*;
@Mapper(
		componentModel = "spring",
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		builder = @Builder(disableBuilder = true)
)
public interface StudentProfileMapper {

		@Mapping(target = "id",                ignore = true)
		@Mapping(target = "student",           ignore = true)
		@Mapping(target = "profilePictureUrl", ignore = true)
		@Mapping(target = "deleted",          ignore  = true)
		@Mapping(target = "deletedAt",         ignore = true)
		@Mapping(target = "createdAt",         ignore = true)
		@Mapping(target = "updatedAt",         ignore = true)
		StudentProfile toEntity( StudentProfileRequestDto request);
		
		@Mapping(target = "studentId",  source = "student.id")
		@Mapping(target = "firstName",  source = "student.firstName")
		@Mapping(target = "lastName",   source = "student.lastName")
		@Mapping(target = "email",      source = "student.email")
		StudentProfileResponseDto toResponseDto(StudentProfile studentProfile);
		
		@Mapping(target = "studentId",  source = "student.id")
		@Mapping(target = "firstName",  source = "student.firstName")
		@Mapping(target = "lastName",   source = "student.lastName")
		@Mapping(target = "email",      source = "student.email")
		StudentProfileResponseDto toSummaryDto(StudentProfile studentProfile);
		
		@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
		@Mapping(target = "id",                ignore = true)
		@Mapping(target = "student",           ignore = true)
		@Mapping(target = "profilePictureUrl", ignore = true)
		@Mapping(target = "deleted",           ignore = true)
		@Mapping(target = "deletedAt",         ignore = true)
		@Mapping(target = "createdAt",         ignore = true)
		@Mapping(target = "updatedAt",         ignore = true)
		void updateEntityFromDto(StudentProfileRequestDto request, @MappingTarget StudentProfile studentProfile);
}