package com.leedTech.studentFeeOneTimePayment.mapper.teacherProfileMapper;
import com.leedTech.studentFeeOneTimePayment.dto.teacherProfile.TeacherProfileRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.teacherProfile.TeacherProfileResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.teacherProfile.TeacherProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TeacherProfileMapper {

			@Mapping(target = "id",                ignore = true)
			@Mapping(target = "teacher",           ignore = true)
			@Mapping(target = "profilePictureUrl", ignore = true)
			@Mapping(target = "active",            ignore = true)
			@Mapping(target = "deleted",           ignore = true)
			@Mapping(target = "verified",          ignore = true)
			@Mapping(target = "blocked",           ignore = true)
			@Mapping(target = "onLeave",           ignore = true)
			@Mapping(target = "createdAt",         ignore = true)
			@Mapping(target = "updatedAt",         ignore = true)
			@Mapping(target = "deletedAt",         ignore = true)
			@Mapping(target = "blockedAt",         ignore = true)
			TeacherProfile toEntity(TeacherProfileRequestDto request);
			
			@Mapping(target = "teacherId",   source = "teacher.id")
			@Mapping(target = "firstName",   source = "teacher.firstName")
			@Mapping(target = "lastName",    source = "teacher.lastName")
			@Mapping(target = "email",       source = "teacher.email")
			@Mapping(target = "role",        expression = "java(profile.getTeacher().getRole().name())")
			@Mapping(
					target = "fullName",
					expression = "java(profile.getTeacher().getFirstName() + ' ' + profile.getTeacher().getLastName())"
			)
			@Mapping(
					target = "remainingLeaveDays",
					expression = "java(profile.getAnnualLeaveDays() != null && profile.getUsedLeaveDays() != null ? profile.getAnnualLeaveDays() - profile.getUsedLeaveDays() : null)"
			)
			TeacherProfileResponseDto toResponseDto(TeacherProfile profile);
			
			@Mapping(target = "id",                ignore = true)
			@Mapping(target = "teacher",           ignore = true)
			@Mapping(target = "profilePictureUrl", ignore = true)
			@Mapping(target = "active",            ignore = true)
			@Mapping(target = "deleted",           ignore = true)
			@Mapping(target = "verified",          ignore = true)
			@Mapping(target = "blocked",           ignore = true)
			@Mapping(target = "onLeave",           ignore = true)
			@Mapping(target = "createdAt",         ignore = true)
			@Mapping(target = "updatedAt",         ignore = true)
			@Mapping(target = "deletedAt",         ignore = true)
			@Mapping(target = "blockedAt",         ignore = true)
			@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
			void updateEntityFromDto(TeacherProfileRequestDto request, @MappingTarget TeacherProfile profile);
}