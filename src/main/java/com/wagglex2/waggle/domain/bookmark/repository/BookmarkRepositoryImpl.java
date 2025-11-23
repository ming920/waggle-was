package com.wagglex2.waggle.domain.bookmark.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wagglex2.waggle.domain.bookmark.entity.QBookmark.*;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Long> findBookmarkedRecruitmentIdsByUserId(Long userId, RecruitmentCategory category, RecruitmentStatus status, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(eqCategory(category))
                .and(eqStatus(status))
                .and(eqUserId(userId));

        List<Long> targetIds = queryFactory
                .select(bookmark.recruitment.id)
                .from(bookmark)
                .where(builder)
                .orderBy(bookmark.bookmarkedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(bookmark.count())
                .from(bookmark)
                .where(builder);

        return PageableExecutionUtils.getPage(targetIds, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqUserId(Long userId) {
        return bookmark.user.id.eq(userId);
    }

    private BooleanExpression eqCategory(RecruitmentCategory category) {
        return bookmark.recruitment.category.eq(category);
    }

    private BooleanExpression eqStatus(RecruitmentStatus status) {
        if (status == null) {
            return bookmark.recruitment.status.ne(RecruitmentStatus.CANCELED);
        }

        return bookmark.recruitment.status.eq(status);
    }
}
