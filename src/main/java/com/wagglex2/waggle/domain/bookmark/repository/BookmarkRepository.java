package com.wagglex2.waggle.domain.bookmark.repository;

import com.wagglex2.waggle.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserIdAndRecruitmentId(Long userId, Long recruitmentId);
}
