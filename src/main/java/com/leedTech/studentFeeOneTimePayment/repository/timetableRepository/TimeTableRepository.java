package com.leedTech.studentFeeOneTimePayment.repository.timetableRepository;
import com.leedTech.studentFeeOneTimePayment.entity.timetable.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface TimeTableRepository extends JpaRepository< TimeTable, UUID > { }
