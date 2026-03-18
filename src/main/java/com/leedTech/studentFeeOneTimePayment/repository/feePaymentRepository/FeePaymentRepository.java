package com.leedTech.studentFeeOneTimePayment.repository.feePaymentRepository;

import com.leedTech.studentFeeOneTimePayment.entity.feePayment.FeePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, UUID> {
	
	@Query("""
	        SELECT f FROM FeePayment f
	        WHERE f.studentNumber = :studentNumber
	        ORDER BY f.createdAt DESC
	        """)
	List <FeePayment> findAllByStudentNumber( @Param ("studentNumber") String studentNumber);
	
	@Query ("""
	        SELECT f FROM FeePayment f
	        ORDER BY f.createdAt DESC
	        """)
	Page <FeePayment> findAllPayments( Pageable pageable);
}