package com.leedTech.studentFeeOneTimePayment.mapper.courseMapper;

import com.leedTech.studentFeeOneTimePayment.dto.course.CourseRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.course.CourseResponseDto;
import com.leedTech.studentFeeOneTimePayment.dto.course.CourseTeacherDto;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseMapper {

@Mapping(
		target = "fullName",
		expression = "java(user.getFirstName() + ' ' + user.getLastName())"
)
CourseTeacherDto toTeacherDto(User user);

@Mapping(target = "id",                     ignore = true)
@Mapping(target = "teacher",                ignore = true)
@Mapping(target = "createdAt",              ignore = true)
@Mapping(target = "updatedAt",              ignore = true)
@Mapping(target = "deletedAt",              ignore = true)
@Mapping(target = "deleted",                ignore = true)
@Mapping(target = "currentEnrollmentCount", ignore = true)
@Mapping(target = "courseUrl",              ignore = true)
Course toEntity(CourseRequestDto request);

@Mapping(target = "teacher", source = "teacher")
CourseResponseDto toResponseDto(Course course);

@Mapping(target = "id",                     ignore = true)
@Mapping(target = "teacher",                ignore = true)
@Mapping(target = "createdAt",              ignore = true)
@Mapping(target = "updatedAt",              ignore = true)
@Mapping(target = "deletedAt",              ignore = true)
@Mapping(target = "deleted",                ignore = true)
@Mapping(target = "currentEnrollmentCount", ignore = true)
@Mapping(target = "courseUrl",              ignore = true)
@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
void updateEntityFromDto(CourseRequestDto request, @MappingTarget Course course);
}