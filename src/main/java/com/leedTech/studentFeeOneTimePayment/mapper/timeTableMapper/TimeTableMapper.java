package com.leedTech.studentFeeOneTimePayment.mapper.timeTableMapper;

import com.leedTech.studentFeeOneTimePayment.dto.timetable.TimetableRequestDTO;
import com.leedTech.studentFeeOneTimePayment.dto.timetable.TimetableResponseDTO;
import com.leedTech.studentFeeOneTimePayment.entity.timetable.TimeTable;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TimeTableMapper {
		
		
		@Mapping(target = "id",        ignore = true)
		@Mapping(target = "createdAt", ignore = true)
		@Mapping(target = "updatedAt", ignore = true)
		@Mapping(target = "course",    ignore = true)
		@Mapping(target = "teacher",   ignore = true)
		TimeTable toEntity(TimetableRequestDTO dto);
		
		@Mapping(target = "courseId",    source = "course.id")
		@Mapping(target = "courseName",  source = "course.courseName")
		@Mapping(target = "courseCode",  source = "course.courseCode")
		@Mapping(target = "teacherId",   source = "teacher.id")
		@Mapping(target = "teacherName", expression = "java(timeTable.getTeacher().getFirstName() + \" \" + timeTable.getTeacher().getLastName())")
		TimetableResponseDTO toResponseDTO(TimeTable timeTable);
		
		@Mapping(target = "id",        ignore = true)
		@Mapping(target = "createdAt", ignore = true)
		@Mapping(target = "updatedAt", ignore = true)
		@Mapping(target = "course",    ignore = true)
		@Mapping(target = "teacher",   ignore = true)
		void updateEntityFromDTO(TimetableRequestDTO dto, @MappingTarget TimeTable timeTable);
}