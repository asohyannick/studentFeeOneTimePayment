package com.leedTech.studentFeeOneTimePayment.mapper.reviewMapper;

import com.leedTech.studentFeeOneTimePayment.dto.review.ReviewRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.review.ReviewResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.review.Review;
import org.mapstruct.*;

@Mapper (componentModel = "spring")
public interface ReviewMapper {
		
		@Mapping(target = "id",              ignore = true)
		@Mapping(target = "course",          ignore = true)
		@Mapping(target = "student",         ignore = true)
		@Mapping(target = "helpfulCount",    ignore = true)
		@Mapping(target = "reportCount",     ignore = true)
		@Mapping(target = "status",          ignore = true)
		@Mapping(target = "approved",        ignore = true)
		@Mapping(target = "deleted",         ignore = true)
		@Mapping(target = "flagged",         ignore = true)
		@Mapping(target = "rejectionReason", ignore = true)
		@Mapping(target = "adminNote",       ignore = true)
		@Mapping(target = "edited",          ignore = true)
		@Mapping(target = "editedAt",        ignore = true)
		@Mapping(target = "createdAt",       ignore = true)
		@Mapping (target = "updatedAt",       ignore = true)
		@Mapping(target = "deletedAt",       ignore = true)
		Review toEntity( ReviewRequestDto request);
		
		@Mapping(target = "courseId",         source = "course.id")
		@Mapping(target = "courseName",       source = "course.courseName")
		@Mapping(target = "courseCode",       source = "course.courseCode")
		@Mapping(target = "studentId",        source = "student.id")
		@Mapping(target = "studentFirstName", source = "student.firstName")
		@Mapping(target = "studentLastName",  source = "student.lastName")
		@Mapping(target = "studentEmail",     source = "student.email")
		@Mapping(
				target = "studentFullName",
				expression = "java(review.getStudent().getFirstName() + ' ' + review.getStudent().getLastName())"
		)
		ReviewResponseDto toResponseDto( Review review);
		
		@Mapping(target = "id",              ignore = true)
		@Mapping(target = "course",          ignore = true)
		@Mapping(target = "student",         ignore = true)
		@Mapping(target = "helpfulCount",    ignore = true)
		@Mapping(target = "reportCount",     ignore = true)
		@Mapping(target = "status",          ignore = true)
		@Mapping(target = "approved",        ignore = true)
		@Mapping(target = "deleted",         ignore = true)
		@Mapping(target = "flagged",         ignore = true)
		@Mapping(target = "rejectionReason", ignore = true)
		@Mapping(target = "adminNote",       ignore = true)
		@Mapping(target = "edited",          ignore = true)
		@Mapping(target = "editedAt",        ignore = true)
		@Mapping(target = "createdAt",       ignore = true)
		@Mapping(target = "updatedAt",       ignore = true)
		@Mapping(target = "deletedAt",       ignore = true)
		@BeanMapping (nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
		void updateEntityFromDto(ReviewRequestDto request, @MappingTarget Review review);
}