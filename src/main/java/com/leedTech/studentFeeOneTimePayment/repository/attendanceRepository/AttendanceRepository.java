package com.leedTech.studentFeeOneTimePayment.repository.attendanceRepository;

import com.leedTech.studentFeeOneTimePayment.entity.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
public interface AttendanceRepository extends JpaRepository <Attendance, UUID >, JpaSpecificationExecutor <Attendance> {
}
