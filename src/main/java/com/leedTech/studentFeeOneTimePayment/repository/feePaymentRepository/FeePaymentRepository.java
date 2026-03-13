package com.leedTech.studentFeeOneTimePayment.repository.feePaymentRepository;

import com.leedTech.studentFeeOneTimePayment.entity.feePayment.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, UUID> {

List<FeePayment> findByStudentNumber(String studentNumber);
}