package com.leedTech.studentFeeOneTimePayment.mapper.attendanceMapper;

import com.leedTech.studentFeeOneTimePayment.dto.attendance.AttendanceResponseDto;
import com.leedTech.studentFeeOneTimePayment.dto.attendance.CreateAttendanceRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.attendance.UpdateAttendanceRequestDto;
import com.leedTech.studentFeeOneTimePayment.entity.attendance.Attendance;
import org.mapstruct.*;

@Mapper (componentModel = "spring")
public interface AttendanceMapper {
	
			@Mapping(source = "student.id", target = "studentId")
			@Mapping(target = "studentName", expression = "java(attendance.getStudent().getFirstName() + \" \" + attendance.getStudent().getLastName())")
			@Mapping(source = "course.id", target = "courseId")
			@Mapping(source = "course.courseName", target = "courseName")
			@Mapping(source = "markedBy.id", target = "markedById")
			@Mapping(source = "markedBy.firstName", target = "markedByName")
			AttendanceResponseDto toDto( Attendance attendance);
			
			@Mapping(target = "id", ignore = true)
			@Mapping(target = "student", ignore = true)
			@Mapping(target = "course", ignore = true)
			@Mapping(target = "markedBy", ignore = true)
			@Mapping(target = "deleted", constant = "false")
			@Mapping (target = "createdAt", ignore = true)
			@Mapping(target = "updatedAt", ignore = true)
			Attendance toEntity( CreateAttendanceRequestDto dto);
			
			@Mapping(target = "id", ignore = true)
			@Mapping(target = "student", ignore = true)
			@Mapping(target = "course", ignore = true)
			@Mapping(target = "markedBy", ignore = true)
			@Mapping(target = "createdAt", ignore = true)
			@Mapping(target = "updatedAt", ignore = true)
			@BeanMapping (nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
			void updateEntityFromDto( UpdateAttendanceRequestDto dto, @MappingTarget Attendance entity);
}