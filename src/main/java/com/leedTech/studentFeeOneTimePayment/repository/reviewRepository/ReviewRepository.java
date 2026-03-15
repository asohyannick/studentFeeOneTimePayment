package com.leedTech.studentFeeOneTimePayment.repository.reviewRepository;

import com.leedTech.studentFeeOneTimePayment.constant.ReviewStatus;
import com.leedTech.studentFeeOneTimePayment.entity.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID>, JpaSpecificationExecutor<Review> {
		boolean existsByCourseIdAndStudentId(UUID courseId, UUID studentId);
        Optional<Review> findByIdAndDeletedFalse(UUID id);
		Page<Review> findAllByCourseIdAndDeletedFalse(UUID courseId, Pageable pageable);
		Page<Review> findAllByStudentIdAndDeletedFalse(UUID studentId, Pageable pageable);
		Page<Review> findAllByDeletedFalse(Pageable pageable);
		long countByStatusAndDeletedFalse(ReviewStatus status);
		long countByCourseIdAndDeletedFalse(UUID courseId);
		long countByDeletedFalse();
		long countByFlaggedTrueAndDeletedFalse();

		@Query("""
		        SELECT AVG(r.rating) FROM Review r
		        WHERE r.course.id = :courseId
		        AND r.deleted = false
		        AND r.approved = true
		        """)
		Double findAverageRatingByCourseId(@Param("courseId") UUID courseId);

		@Modifying
		@Query("UPDATE Review r SET r.deleted = true, r.deletedAt = CURRENT_TIMESTAMP WHERE r.id = :id")
		void softDeleteById(@Param("id") UUID id);
}