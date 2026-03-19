package com.leedTech.studentFeeOneTimePayment.mapper.assignmentMapper;
import com.leedTech.studentFeeOneTimePayment.dto.assignment.AssignmentRequestDTO;
import com.leedTech.studentFeeOneTimePayment.dto.assignment.AssignmentResponseDTO;
import com.leedTech.studentFeeOneTimePayment.entity.assignment.Assignment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

			@Mapping(target = "id",        ignore = true)
			@Mapping(target = "createdAt", ignore = true)
			@Mapping(target = "updatedAt", ignore = true)
			@Mapping(target = "teacher",   ignore = true)
			@Mapping(target = "student",   ignore = true)
			Assignment toEntity(AssignmentRequestDTO dto);
			
			@Mapping(target = "teacherId",   source = "teacher.id")
			@Mapping(target = "teacherName", expression = "java(assignment.getTeacher().getFirstName() + \" \" + assignment.getTeacher().getLastName())")
			@Mapping(target = "studentId",   source = "student.id")
			@Mapping(target = "studentName", expression = "java(assignment.getStudent().getStudent().getFirstName() + \" \" + assignment.getStudent().getStudent().getLastName())")
			AssignmentResponseDTO toResponseDTO(Assignment assignment);
			
			@Mapping(target = "id",        ignore = true)
			@Mapping(target = "createdAt", ignore = true)
			@Mapping(target = "updatedAt", ignore = true)
			@Mapping(target = "teacher",   ignore = true)
			@Mapping(target = "student",   ignore = true)
			void updateEntityFromDTO(AssignmentRequestDTO dto, @MappingTarget Assignment assignment);
}