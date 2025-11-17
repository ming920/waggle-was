package com.wagglex2.waggle.domain.review.service;

import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.dto.request.ReviewUpdateRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import com.wagglex2.waggle.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Review findById(Long id);
    Long createReview(Long reviewerId, ReviewCreationRequestDto dto);
    Page<ReviewResponseDto> getReviewsByRevieweeId(Long revieweeId, Pageable pageable);
    Page<ReviewResponseDto> getReviewsByReviewerId(Long reviewerId, Pageable pageable);
    Long updateReview(Long userId, Long reviewId, ReviewUpdateRequestDto dto);
    void deleteReview(Long userId, Long reviewId);
}
