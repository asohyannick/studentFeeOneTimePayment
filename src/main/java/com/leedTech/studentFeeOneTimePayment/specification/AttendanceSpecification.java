package com.leedTech.studentFeeOneTimePayment.specification;

import com.leedTech.studentFeeOneTimePayment.dto.attendance.AttendanceFilterDto;
import com.leedTech.studentFeeOneTimePayment.entity.attendance.Attendance;
import org.springframework.data.jpa.domain.Specification;

public class AttendanceSpecification {

	
	public static Specification<Attendance> build( AttendanceFilterDto filter) {
		Specification<Attendance> spec = null;
		
		if (filter.studentId() != null) {
			Specification<Attendance> studentSpec = (root, query, cb) ->
					                                        cb.equal(root.get("student").get("id"), filter.studentId());
			spec = (spec == null) ? studentSpec : spec.and(studentSpec);
		}
		
		if (filter.courseId() != null) {
			Specification<Attendance> courseSpec = (root, query, cb) ->
					                                       cb.equal(root.get("course").get("id"), filter.courseId());
			spec = (spec == null) ? courseSpec : spec.and(courseSpec);
		}
		
		if (filter.markedById() != null) {
			Specification<Attendance> markedBySpec = (root, query, cb) ->
					                                         cb.equal(root.get("markedBy").get("id"), filter.markedById());
			spec = (spec == null) ? markedBySpec : spec.and(markedBySpec);
		}
		
		return spec;
	}
}