package com.leedTech.studentFeeOneTimePayment.dto.attendance;
import com.leedTech.studentFeeOneTimePayment.constant.AttendanceMethod;
import com.leedTech.studentFeeOneTimePayment.constant.AttendanceStatus;
import com.leedTech.studentFeeOneTimePayment.constant.ScheduleDayType;
import com.leedTech.studentFeeOneTimePayment.constant.SessionType;
import org.springdoc.core.annotations.ParameterObject;
import java.time.LocalDate;
import java.util.UUID;

@ParameterObject
public record AttendanceFilterDto(
		
		UUID studentId,
		UUID courseId,
		UUID markedById,
		
		LocalDate attendanceDate,
		
		AttendanceStatus status,
		
		String academicYear,
		String semester,
		String currentClass,
		String subject,
		
		SessionType sessionType,
		ScheduleDayType dayOfWeek,
		AttendanceMethod method,
		
		Boolean verified
) {}