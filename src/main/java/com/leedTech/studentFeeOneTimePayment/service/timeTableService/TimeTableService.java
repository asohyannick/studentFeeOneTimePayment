package com.leedTech.studentFeeOneTimePayment.service.timeTableService;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.leedTech.studentFeeOneTimePayment.dto.timetable.TimetableRequestDTO;
import com.leedTech.studentFeeOneTimePayment.dto.timetable.TimetableResponseDTO;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import com.leedTech.studentFeeOneTimePayment.entity.timetable.TimeTable;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.mapper.timeTableMapper.TimeTableMapper;
import com.leedTech.studentFeeOneTimePayment.repository.courseRepository.CourseRepository;
import com.leedTech.studentFeeOneTimePayment.repository.timetableRepository.TimeTableRepository;
import com.leedTech.studentFeeOneTimePayment.repository.userRepository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeTableService {
		
		private final TimeTableRepository timeTableRepository;
		private final TimeTableMapper     timeTableMapper;
		private final CourseRepository    courseRepository;
		private final UserRepository      userRepository;
		
		@Transactional
		public TimetableResponseDTO createTimeTable(TimetableRequestDTO dto) {
			log.info("Creating timetable entry for class: {}", dto.className());
			
			Course course = courseRepository.findById(dto.courseId())
					                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + dto.courseId()));
			
			User teacher = userRepository.findById(dto.teacherId())
					               .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + dto.teacherId()));
			
			TimeTable timeTable = timeTableMapper.toEntity(dto);
			timeTable.setCourse(course);
			timeTable.setTeacher(teacher);
			
			TimeTable saved = timeTableRepository.save(timeTable);
			log.info("Timetable entry created successfully with id: {}", saved.getId());
			
			return timeTableMapper.toResponseDTO(saved);
		}
		
		@Transactional(readOnly = true)
		public List<TimetableResponseDTO> getAllTimeTables() {
			log.info("Fetching all timetable entries");
			
			return timeTableRepository.findAll()
					       .stream()
					       .map(timeTableMapper::toResponseDTO)
					       .toList();
		}
		
		
		@Transactional(readOnly = true)
		public TimetableResponseDTO getTimeTableById(UUID id) {
			log.info("Fetching timetable entry with id: {}", id);
			
			TimeTable timeTable = timeTableRepository.findById(id)
					                      .orElseThrow(() -> new EntityNotFoundException("Timetable entry not found with id: " + id));
			
			return timeTableMapper.toResponseDTO(timeTable);
		}
		
		
		@Transactional
		public TimetableResponseDTO updateTimeTable(UUID id, TimetableRequestDTO dto) {
			log.info("Updating timetable entry with id: {}", id);
			
			TimeTable timeTable = timeTableRepository.findById(id)
					                      .orElseThrow(() -> new EntityNotFoundException("Timetable entry not found with id: " + id));
			
			if (!timeTable.getCourse().getId().equals(dto.courseId())) {
				Course course = courseRepository.findById(dto.courseId())
						                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + dto.courseId()));
				timeTable.setCourse(course);
			}
			
			if (!timeTable.getTeacher().getId().equals(dto.teacherId())) {
				User teacher = userRepository.findById(dto.teacherId())
						               .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + dto.teacherId()));
				timeTable.setTeacher(teacher);
			}
			
			timeTableMapper.updateEntityFromDTO(dto, timeTable);
			
			TimeTable updated = timeTableRepository.save(timeTable);
			log.info("Timetable entry updated successfully with id: {}", updated.getId());
			
			return timeTableMapper.toResponseDTO(updated);
		}
		
		@Transactional
		public void deleteTimeTable(UUID id) {
			log.info("Deleting timetable entry with id: {}", id);
			
			if (!timeTableRepository.existsById(id)) {
				throw new EntityNotFoundException("Timetable entry not found with id: " + id);
			}
			
			timeTableRepository.deleteById(id);
			log.info("Timetable entry deleted successfully with id: {}", id);
		}
		
		@Transactional(readOnly = true)
		public long countTimeTables() {
			long count = timeTableRepository.count();
			log.info("Total timetable entries count: {}", count);
			return count;
		}
		
		
		@Transactional(readOnly = true)
		public byte[] downloadTimetableAsPdf() {
			log.info("Generating timetable PDF");
			
			List<TimeTable> timeTables = timeTableRepository.findAll();
			
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				
				PdfWriter writer       = new PdfWriter(outputStream);
				PdfDocument pdfDoc     = new PdfDocument(writer);
				Document document      = new Document(pdfDoc);
				
				DeviceRgb headerColor  = new DeviceRgb(31, 56, 100);
				DeviceRgb rowEven      = new DeviceRgb(235, 241, 251);
				DeviceRgb rowOdd       = new DeviceRgb(255, 255, 255);
				
				DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
				
				document.add(new Paragraph("School Timetable")
						             .setFontSize(22)
						             .setTextAlignment(TextAlignment.CENTER)
						             .setFontColor(headerColor)
						             .setMarginBottom(4));
				
				document.add(new Paragraph("Generated on: " + java.time.LocalDate.now())
						             .setFontSize(10)
						             .setTextAlignment(TextAlignment.CENTER)
						             .setFontColor(ColorConstants.GRAY)
						             .setMarginBottom(16));
				
				float[] columnWidths = {1.5f, 2f, 1.2f, 1.2f, 2f, 2f, 1.5f, 1.5f, 1.5f, 1.5f};
				Table table = new Table(UnitValue.createPercentArray(columnWidths))
						              .useAllAvailableWidth();
				
				String[] headers = {
						"Day", "Course", "Code", "Period",
						"Start", "End", "Class", "Room",
						"Teacher", "Term"
				};
				
				for (String header : headers) {
					table.addHeaderCell(new Cell()
							                    .add(new Paragraph(header).setFontSize(9).setFontColor(ColorConstants.WHITE))
							                    .setBackgroundColor(headerColor)
							                    .setTextAlignment(TextAlignment.CENTER)
							                    .setPadding(6));
				}
				
				for (int i = 0; i < timeTables.size(); i++) {
					TimeTable tt      = timeTables.get(i);
					DeviceRgb rowColor = (i % 2 == 0) ? rowEven : rowOdd;
					
					String[] cells = {
							tt.getDayOfWeek() != null    ? tt.getDayOfWeek().name()                                                    : "-",
							tt.getCourse() != null        ? tt.getCourse().getCourseName()                                              : "-",
							tt.getCourse() != null        ? tt.getCourse().getCourseCode()                                              : "-",
							tt.getPeriodNumber() != null  ? String.valueOf(tt.getPeriodNumber())                                        : "-",
							tt.getStartTime() != null     ? tt.getStartTime().format(timeFmt)                                           : "-",
							tt.getEndTime() != null       ? tt.getEndTime().format(timeFmt)                                             : "-",
							tt.getClassName() != null     ? tt.getClassName()                                                           : "-",
							tt.getRoomNumber() != null    ? tt.getRoomNumber()                                                          : "-",
							tt.getTeacher() != null       ? tt.getTeacher().getFirstName() + " " + tt.getTeacher().getLastName()       : "-",
							tt.getTerm() != null          ? tt.getTerm().name()                                                         : "-",
					};
					
					for (String cellValue : cells) {
						table.addCell(new Cell()
								              .add(new Paragraph(cellValue).setFontSize(8))
								              .setBackgroundColor(rowColor)
								              .setTextAlignment(TextAlignment.CENTER)
								              .setPadding(5));
					}
				}
				
				document.add(table);
				
				document.add(new Paragraph("Total entries: " + timeTables.size())
						             .setFontSize(9)
						             .setFontColor(ColorConstants.GRAY)
						             .setTextAlignment(TextAlignment.RIGHT)
						             .setMarginTop(10));
				
				document.close();
				
				log.info("Timetable PDF generated successfully with {} entries", timeTables.size());
				return outputStream.toByteArray();
				
			} catch (Exception e) {
				log.error("Failed to generate timetable PDF: {}", e.getMessage());
				throw new RuntimeException("Failed to generate timetable PDF", e);
			}
		}
}