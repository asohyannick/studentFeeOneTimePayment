package com.leedTech.studentFeeOneTimePayment.service.attendanceService;
import com.leedTech.studentFeeOneTimePayment.dto.attendance.AttendanceResponseDto;
import com.leedTech.studentFeeOneTimePayment.dto.attendance.CreateAttendanceRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.attendance.UpdateAttendanceRequestDto;
import com.leedTech.studentFeeOneTimePayment.entity.attendance.Attendance;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.mapper.attendanceMapper.AttendanceMapper;
import com.leedTech.studentFeeOneTimePayment.repository.attendanceRepository.AttendanceRepository;
import com.leedTech.studentFeeOneTimePayment.repository.courseRepository.CourseRepository;
import com.leedTech.studentFeeOneTimePayment.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceService {

		private final AttendanceRepository attendanceRepository;
		private final AttendanceMapper attendanceMapper;
		private final UserRepository userRepository;
		private final CourseRepository courseRepository;
		
		@Transactional
		public AttendanceResponseDto createAttendance( CreateAttendanceRequestDto request) {
			log.info("Attempting to create attendance for student ID: {} in course ID: {}", request.studentId(), request.courseId());
			
			User student = userRepository.findById(request.studentId())
					               .orElseThrow(() -> new NotFoundException("Student not found"));
			
			Course course = courseRepository.findById(request.courseId())
					                .orElseThrow(() -> new NotFoundException("Course not found"));
			
			User markedBy = userRepository.findById(request.markedById())
					                .orElseThrow(() -> new NotFoundException("Staff member not found"));
			
			Attendance attendance = attendanceMapper.toEntity(request);
			attendance.setStudent(student);
			attendance.setCourse(course);
			attendance.setMarkedBy(markedBy);
			
			Attendance savedAttendance = attendanceRepository.save(attendance);
			log.info("Attendance has been created successfully: {}", savedAttendance.getId());
			
			return attendanceMapper.toDto(savedAttendance);
		}
		
		@Transactional(readOnly = true)
		public Page <AttendanceResponseDto> getAllAttendances(
				Specification<Attendance> spec,
				Pageable pageable) {
			
			log.debug("Fetching paginated attendances with filters and sorting: {}", pageable);
			
			return attendanceRepository
					       .findAll(spec, pageable)
					       .map(attendanceMapper::toDto);
		}
		
		@Transactional(readOnly = true)
		public AttendanceResponseDto getAttendanceById( UUID id) {
			log.debug("Fetching attendance details for ID: {}", id);
			return attendanceRepository.findById(id)
					       .map(attendanceMapper::toDto)
					       .orElseThrow(() -> {
						       log.error("Attendance record not found with ID: {}", id);
						       return new NotFoundException("Attendance record not found with ID: " + id);
					       });
		}

		@Transactional
		public AttendanceResponseDto updateAttendance( UpdateAttendanceRequestDto request) {
			log.info("Updating attendance record for ID: {}", request.attendanceId());
			
			Attendance attendance = attendanceRepository.findById(request.attendanceId())
					                        .orElseThrow(() -> {
						                        log.error("Update failed: Attendance not found for ID: {}", request.attendanceId());
						                        return new NotFoundException("Attendance not found");
					                        });
			
			attendanceMapper.updateEntityFromDto(request, attendance);
			Attendance updatedAttendance = attendanceRepository.save(attendance);
			
			log.info("Attendance ID: {} updated successfully", updatedAttendance.getId());
			return attendanceMapper.toDto(updatedAttendance);
		}
		
		@Transactional(readOnly = true)
		public long countAllAttendances() {
			long count = attendanceRepository.count();
			log.debug("Total attendance records counted: {}", count);
			return count;
		}
		
		@Transactional
		public void deleteAttendance(UUID id) {
			log.warn("Soft deleting attendance record with ID: {}", id);
			
			Attendance attendance = attendanceRepository.findById(id)
					                        .orElseThrow(() -> {
						                        log.error("Delete failed: Attendance not found for ID: {}", id);
						                        return new NotFoundException("Attendance not found");
					                        });
			
			attendance.setDeleted(true);
			attendance.setDeletedAt(java.time.Instant.now());
			attendanceRepository.save(attendance);
			
			log.info("Attendance ID: {} marked as deleted", id);
		}

}