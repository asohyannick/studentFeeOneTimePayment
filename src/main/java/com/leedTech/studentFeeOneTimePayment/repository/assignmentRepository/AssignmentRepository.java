package com.leedTech.studentFeeOneTimePayment.repository.assignmentRepository;
import com.leedTech.studentFeeOneTimePayment.entity.assignment.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AssignmentRepository extends JpaRepository< Assignment, UUID > { }
