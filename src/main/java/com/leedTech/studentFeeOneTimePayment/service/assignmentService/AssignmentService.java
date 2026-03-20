package com.leedTech.studentFeeOneTimePayment.service.assignmentService;
import com.leedTech.studentFeeOneTimePayment.dto.assignment.AssignmentRequestDTO;
import com.leedTech.studentFeeOneTimePayment.dto.assignment.AssignmentResponseDTO;
import com.leedTech.studentFeeOneTimePayment.entity.assignment.Assignment;
import com.leedTech.studentFeeOneTimePayment.entity.studentProfile.StudentProfile;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.mapper.assignmentMapper.AssignmentMapper;
import com.leedTech.studentFeeOneTimePayment.repository.assignmentRepository.AssignmentRepository;
import com.leedTech.studentFeeOneTimePayment.repository.studentProfileRepository.StudentProfileRepository;
import com.leedTech.studentFeeOneTimePayment.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

	private final AssignmentRepository assignmentRepository;
	private final UserRepository userRepository;
	private final StudentProfileRepository studentProfileRepository;
	private final AssignmentMapper assignmentMapper;
	
	@Transactional
	public AssignmentResponseDTO createAssignment(AssignmentRequestDTO dto) {
		log.info("Creating assignment with name: {}", dto.name());
		
		User teacher = userRepository.findById(dto.teacherId())
				               .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + dto.teacherId()));
		
		StudentProfile student = studentProfileRepository.findById(dto.studentId())
				                         .orElseThrow(() -> new NotFoundException ("Student not found with id: " + dto.studentId()));
		
		Assignment assignment = assignmentMapper.toEntity(dto);
		assignment.setTeacher(teacher);
		assignment.setStudent(student);
		
		Assignment saved = assignmentRepository.save(assignment);
		log.info("Assignment created successfully with id: {}", saved.getId());
		
		return assignmentMapper.toResponseDTO(saved);
	}
	
	@Transactional(readOnly = true)
	public List<AssignmentResponseDTO> getAllAssignments() {
		log.info("Fetching all assignments");
		
		return assignmentRepository.findAll()
				       .stream()
				       .map(assignmentMapper::toResponseDTO)
				       .toList();
	}
	
	@Transactional(readOnly = true)
	public AssignmentResponseDTO getAssignmentById(UUID id) {
		log.info("Fetching assignment with id: {}", id);
		
		Assignment assignment = assignmentRepository.findById(id)
				                        .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + id));
		
		return assignmentMapper.toResponseDTO(assignment);
	}
	
	@Transactional
	public AssignmentResponseDTO updateAssignment(UUID id, AssignmentRequestDTO dto) {
		log.info("Updating assignment with id: {}", id);
		
		Assignment assignment = assignmentRepository.findById(id)
				                        .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + id));
		
		if (!assignment.getTeacher().getId().equals(dto.teacherId())) {
			User teacher = userRepository.findById(dto.teacherId())
					               .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + dto.teacherId()));
			assignment.setTeacher(teacher);
		}
		
		if (!assignment.getStudent().getId().equals(dto.studentId())) {
			StudentProfile student = studentProfileRepository.findById(dto.studentId())
					                         .orElseThrow(() -> new NotFoundException("Student not found with id: " + dto.studentId()));
			assignment.setStudent(student);
		}
		
		assignmentMapper.updateEntityFromDTO(dto, assignment);
		
		Assignment updated = assignmentRepository.save(assignment);
		log.info("Assignment updated successfully with id: {}", updated.getId());
		
		return assignmentMapper.toResponseDTO(updated);
	}
	
	@Transactional
	public void deleteAssignment(UUID id) {
		log.info("Deleting assignment with id: {}", id);
		
		if (!assignmentRepository.existsById(id)) {
			throw new NotFoundException("Assignment not found with id: " + id);
		}
		
		assignmentRepository.deleteById(id);
		log.info("Assignment deleted successfully with id: {}", id);
	}
	
	
	@Transactional(readOnly = true)
	public long countAssignments() {
		long count = assignmentRepository.count();
		log.info("Total assignments count: {}", count);
		return count;
	}
}
