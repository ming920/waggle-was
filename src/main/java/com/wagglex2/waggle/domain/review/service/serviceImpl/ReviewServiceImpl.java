package com.wagglex2.waggle.domain.review.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.dto.request.ReviewUpdateRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import com.wagglex2.waggle.domain.review.entity.Review;
import com.wagglex2.waggle.domain.review.entity.type.ReviewStatus;
import com.wagglex2.waggle.domain.review.repository.ReviewRepository;
import com.wagglex2.waggle.domain.review.service.ReviewService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;


    @Override
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
    }

    /**
     * 리뷰를 생성하는 서비스 로직.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>작성자(authorId)가 자기 자신에게 리뷰를 남기려는 경우 예외 발생</li>
     *   <li>작성자(User)와 리뷰 대상(User) 엔티티 조회</li>
     *   <li>리뷰 생성 요청 DTO를 기반으로 Review 엔티티 생성</li>
     *   <li>생성된 Review 엔티티를 저장 후 식별자(ID) 반환</li>
     * </ol>
     *
     * @param reviewerId 리뷰 작성자 ID
     * @param dto        리뷰 생성 요청 DTO (리뷰 대상 사용자 ID, 내용)
     * @return 생성된 리뷰의 ID
     * @throws BusinessException 자기 자신에게 리뷰를 남기려는 경우 발생
     */
    @Override
    @Transactional
    public Long createReview(Long reviewerId, ReviewCreationRequestDto dto) {

        if (reviewerId.equals(dto.revieweeId())) {
            throw new BusinessException(ErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }

        User reviewer = userService.findById(reviewerId);
        User reviewee = userService.findById(dto.revieweeId());

        Review review = dto.toEntity(reviewer, reviewee, dto.content());
        return reviewRepository.save(review).getId();
    }

    /**
     * 특정 사용자가 <b>받은 리뷰 목록</b>을 {@link Pageable} 조건에 따라 페이지네이션 방식으로 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>컨트롤러에서 전달된 {@link Pageable} 객체를 기반으로 페이징 및 정렬 조건을 설정한다.</li>
     *   <li>{@code revieweeId}에 해당하는 리뷰를 {@link ReviewRepository#findByRevieweeIdAndStatus(Long, ReviewStatus, Pageable)}로 조회한다.</li>
     *   <li>조회된 {@link Review} 엔티티를 {@link ReviewResponseDto}로 변환한다.</li>
     * </ol>
     *
     * @param revieweeId 리뷰 대상 사용자의 고유 ID
     * @param pageable   페이징 및 정렬 정보 (page, size, sort 등)
     * @return 페이지 정보와 함께 {@link ReviewResponseDto}
     */
    @Override
    public Page<ReviewResponseDto> getReviewsByRevieweeId(Long revieweeId, Pageable pageable) {
        return reviewRepository.findByRevieweeIdAndStatus(
                        revieweeId,
                        ReviewStatus.ACTIVE,
                        pageable
                )
                .map(ReviewResponseDto::from);
    }

    /**
     * 특정 사용자가 <b>작성한 리뷰 목록</b>을 {@link Pageable} 조건에 따라 페이지네이션 방식으로 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>컨트롤러에서 전달된 {@link Pageable} 객체를 기반으로 페이징 및 정렬 조건을 설정한다.</li>
     *   <li>{@code reviewerId}에 해당하는 리뷰를 {@link ReviewRepository#findByReviewerIdAndStatus(Long, ReviewStatus, Pageable)}로 조회한다.</li>
     *   <li>조회된 {@link Review} 엔티티를 {@link ReviewResponseDto}로 변환한다.</li>
     * </ol>
     *
     * @param reviewerId 리뷰 작성 사용자의 고유 ID
     * @param pageable   페이징 및 정렬 정보 (page, size, sort 등)
     * @return 페이지 정보와 함께 {@link ReviewResponseDto}
     */
    @Override
    public Page<ReviewResponseDto> getReviewsByReviewerId(Long reviewerId, Pageable pageable) {
        return reviewRepository.findByReviewerIdAndStatus(
                        reviewerId,
                        ReviewStatus.ACTIVE,
                        pageable
                )
                .map(ReviewResponseDto::from);
    }


    /**
     * 리뷰 수정 서비스 로직
     *
     * <p>
     * 사용자가 작성한 리뷰 내용을 수정한다.
     * 단, 삭제된 리뷰나 본인 소유가 아닌 리뷰는 수정할 수 없다.
     * <p>
     * - 메서드 내부에서 userId와 작성자 일치 여부를 추가 검증 (본인 리뷰만 수정 가능)
     */
    @Override
    @Transactional
    @PreAuthorize("#userId == authentication.principal.userId")
    public Long updateReview(Long userId, Long reviewId, ReviewUpdateRequestDto dto) {

        Review review = findById(reviewId);

        if (!userId.equals(review.getReviewer().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_UPDATE_ANOTHER_USER_REVIEW);
        }

        if (review.getStatus() != ReviewStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.CANNOT_UPDATE_NOT_ACTIVE_REVIEW);
        }

        review.update(dto);
        return review.getId();
    }

    /**
     * 리뷰 삭제 서비스 로직 (Soft Delete)
     *
     * <p>
     * 사용자가 작성한 리뷰를 삭제 처리한다.
     * <br>
     * 실제 DB에서 물리적으로 삭제하지 않고, 상태를 {@link ReviewStatus#DELETED} 로 변경한다.
     * <ul>
     *     <li>현재 로그인한 사용자(userId)가 리뷰 작성자와 동일할 것</li>
     *     <li>리뷰 상태가 {@link ReviewStatus#ACTIVE} 일 것</li>
     * </ul>
     *
     * @param userId   현재 로그인한 사용자 ID
     * @param reviewId 삭제할 리뷰 ID
     * @throws BusinessException 본인 리뷰가 아니거나, 이미 삭제된 리뷰를 삭제하려 할 경우 예외 발생
     */
    @Override
    @Transactional
    @PreAuthorize("#userId == authentication.principal.userId")
    public void deleteReview(Long userId, Long reviewId) {

        Review review = findById(reviewId);

        if (!userId.equals(review.getReviewer().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ANOTHER_USER_REVIEW);
        }

        if (review.getStatus() != ReviewStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_NOT_ACTIVE_REVIEW);
        }

        review.delete();
    }
}
