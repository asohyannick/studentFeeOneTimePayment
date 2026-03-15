package com.leedTech.studentFeeOneTimePayment.service.reviewService;
import com.leedTech.studentFeeOneTimePayment.constant.ReviewStatus;
import com.leedTech.studentFeeOneTimePayment.dto.review.ReviewRequestDto;
import com.leedTech.studentFeeOneTimePayment.dto.review.ReviewResponseDto;
import com.leedTech.studentFeeOneTimePayment.entity.course.Course;
import com.leedTech.studentFeeOneTimePayment.entity.review.Review;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.mapper.reviewMapper.ReviewMapper;
import com.leedTech.studentFeeOneTimePayment.repository.courseRepository.CourseRepository;
import com.leedTech.studentFeeOneTimePayment.repository.reviewRepository.ReviewRepository;
import com.leedTech.studentFeeOneTimePayment.repository.userRepository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

		private final ReviewRepository reviewRepository;
		private final CourseRepository courseRepository;
		private final UserRepository   userRepository;
		private final ReviewMapper     reviewMapper;
		
		private Review findReviewById(UUID reviewId) {
			return reviewRepository.findByIdAndDeletedFalse(reviewId)
					       .orElseThrow(() -> new NotFoundException(
							       "Review not found with id: " + reviewId
					       ));
		}
		
		private Course findCourseById(UUID courseId) {
			return courseRepository.findByIdAndDeletedFalse(courseId)
					       .orElseThrow(() -> new NotFoundException(
							       "Course not found with id: " + courseId
					       ));
		}
		
		private User findStudentById(UUID studentId) {
			return userRepository.findById(studentId)
					       .orElseThrow(() -> new NotFoundException(
							       "Student not found with id: " + studentId
					       ));
		}
		
		@Transactional
		public ReviewResponseDto createReview(ReviewRequestDto request) {
			
			if (request.rating() < 1 || request.rating() > 5) {
				throw new BadRequestException("Rating must be between 1 and 5");
			}
			
			Course course = findCourseById(request.courseId());
			
			User student = findStudentById(request.studentId());
			
			if (reviewRepository.existsByCourseIdAndStudentId(
					request.courseId(), request.studentId())) {
				throw new BadRequestException(
						"You have already submitted a review for this course"
				);
			}
			
			Review review = reviewMapper.toEntity(request);
			review.setCourse(course);
			review.setStudent(student);
			review.setStatus(ReviewStatus.APPROVED);
			
			Review saved = reviewRepository.save(review);
			log.info("Review created for course: {} by student: {}",
					request.courseId(), request.studentId());
			return reviewMapper.toResponseDto(saved);
		}
		
		@Transactional(readOnly = true)
		public Page<ReviewResponseDto> fetchAllReviews(
				int page, int size, String sortBy, String sortDir
		) {
			Sort sort = sortDir.equalsIgnoreCase("asc")
					            ? Sort.by(sortBy).ascending()
					            : Sort.by(sortBy).descending();
			Pageable pageable = PageRequest.of(page, size, sort);
			return reviewRepository.findAllByDeletedFalse(pageable)
					       .map(reviewMapper::toResponseDto);
		}

		@Transactional(readOnly = true)
		public Page<ReviewResponseDto> fetchReviewsByCourse(
				UUID courseId, int page, int size, String sortBy, String sortDir
		) {
			findCourseById(courseId);
			Sort sort = sortDir.equalsIgnoreCase("asc")
					            ? Sort.by(sortBy).ascending()
					            : Sort.by(sortBy).descending();
			Pageable pageable = PageRequest.of(page, size, sort);
			
			return reviewRepository.findAllByCourseIdAndDeletedFalse(courseId, pageable)
					       .map(reviewMapper::toResponseDto);
		}
		
		@Transactional(readOnly = true)
		public Page<ReviewResponseDto> fetchReviewsByStudent(
				UUID studentId, int page, int size, String sortBy, String sortDir
		) {
			findStudentById(studentId);
			Sort sort = sortDir.equalsIgnoreCase("asc")
					            ? Sort.by(sortBy).ascending()
					            : Sort.by(sortBy).descending();
			Pageable pageable = PageRequest.of(page, size, sort);
			return reviewRepository.findAllByStudentIdAndDeletedFalse(studentId, pageable)
					       .map(reviewMapper::toResponseDto);
		}
		
		@Transactional(readOnly = true)
		public ReviewResponseDto fetchReviewById(UUID reviewId) {
			return reviewMapper.toResponseDto(findReviewById(reviewId));
		}
		
		@Transactional
		public ReviewResponseDto updateReview(UUID reviewId, ReviewRequestDto request) {
			Review review = findReviewById(reviewId);
			
			if (request.rating() < 1 || request.rating() > 5) {
				throw new BadRequestException("Rating must be between 1 and 5");
			}
			
			reviewMapper.updateEntityFromDto(request, review);
			
			review.setEdited(true);
			review.setEditedAt(Instant.now());
			
			review.setStatus(ReviewStatus.PENDING);
			review.setApproved(false);
			
			Review updated = reviewRepository.save(review);
			log.info("Review updated: {}", reviewId);
			return reviewMapper.toResponseDto(updated);
		}
		
		@Transactional
		public void deleteReview(UUID reviewId) {
			findReviewById(reviewId);
			reviewRepository.softDeleteById(reviewId);
			log.info("Review soft deleted: {}", reviewId);
		}
		
		@Transactional(readOnly = true)
		public long countAllReviews() {
			return reviewRepository.countByDeletedFalse();
		}

		@Transactional(readOnly = true)
		public long countReviewsByCourse(UUID courseId) {
			return reviewRepository.countByCourseIdAndDeletedFalse(courseId);
		}
		
		@Transactional(readOnly = true)
		public long countPendingReviews() {
			return reviewRepository.countByStatusAndDeletedFalse(ReviewStatus.PENDING);
		}
		
		@Transactional(readOnly = true)
		public long countFlaggedReviews() {
			return reviewRepository.countByFlaggedTrueAndDeletedFalse();
		}
		
		@Transactional(readOnly = true)
		public Double getAverageRatingForCourse(UUID courseId) {
			findCourseById(courseId);
			Double avg = reviewRepository.findAverageRatingByCourseId(courseId);
			return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
		}

		@Transactional(readOnly = true)
		public Page<ReviewResponseDto> searchAndFilterReviews(
				String keyword,
				UUID courseId,
				String courseCode,
				UUID studentId,
				Integer rating,
				ReviewStatus status,
				Boolean isApproved,
				Boolean isFlagged,
				Boolean isRecommended,
				Boolean isEdited,
				int page, int size, String sortBy, String sortDir
		) {
			Sort sort = sortDir.equalsIgnoreCase("asc")
					            ? Sort.by(sortBy).ascending()
					            : Sort.by(sortBy).descending();
			Pageable pageable = PageRequest.of(page, size, sort);
			
			Specification<Review> spec = buildSpecification(
					keyword, courseId, courseCode, studentId, rating, status,
					isApproved, isFlagged, isRecommended, isEdited
			);
			
			return reviewRepository.findAll(spec, pageable)
					       .map(reviewMapper::toResponseDto);
		}

		private Specification<Review> buildSpecification(
				String keyword,
				UUID courseId,
				String courseCode,
				UUID studentId,
				Integer rating,
				ReviewStatus status,
				Boolean isApproved,
				Boolean isFlagged,
				Boolean isRecommended,
				Boolean isEdited
		) {
			return (root, query, cb) -> {
				List<Predicate> predicates = new ArrayList<>();
				
				predicates.add(cb.isFalse(root.get("deleted")));
				
				if (keyword != null && !keyword.isBlank()) {
					String pattern = "%" + keyword.toLowerCase() + "%";
					predicates.add(cb.or(
							cb.like(cb.lower(root.get("comment")), pattern),
							cb.like(cb.lower(root.get("title")),   pattern),
							cb.like(cb.lower(root.get("pros")),    pattern),
							cb.like(cb.lower(root.get("cons")),    pattern)
					));
				}
				
				if (courseId   != null) predicates.add(cb.equal(root.get("course").get("id"),   courseId));
				if (studentId  != null) predicates.add(cb.equal(root.get("student").get("id"),  studentId));
				if (rating     != null) predicates.add(cb.equal(root.get("rating"),             rating));
				if (status     != null) predicates.add(cb.equal(root.get("status"),             status));
				
				if (courseCode != null && !courseCode.isBlank()) {
					predicates.add(cb.equal(
							cb.lower(root.get("course").get("courseCode")),
							courseCode.trim().toLowerCase()
					));
				}
				
				if (isApproved    != null) predicates.add(cb.equal(root.get("approved"),    isApproved));
				if (isFlagged     != null) predicates.add(cb.equal(root.get("flagged"),     isFlagged));
				if (isRecommended != null) predicates.add(cb.equal(root.get("recommended"), isRecommended));
				if (isEdited      != null) predicates.add(cb.equal(root.get("edited"),      isEdited));
				
				return cb.and(predicates.toArray(new Predicate[0]));
			};
		}
}