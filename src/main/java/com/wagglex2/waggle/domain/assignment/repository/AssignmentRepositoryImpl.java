package com.wagglex2.waggle.domain.assignment.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentSearchCondition;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentSummaryResponseDto;
import com.wagglex2.waggle.domain.common.querydsl.RecruitmentSearchMatcher;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.wagglex2.waggle.domain.assignment.entity.QAssignment.assignment;
import static com.wagglex2.waggle.domain.bookmark.entity.QBookmark.bookmark;
import static com.wagglex2.waggle.domain.user.entity.QUser.user;

/**
 * AssignmentRepositoryCustom 인터페이스의 구현체
 */
@Repository
@RequiredArgsConstructor
public class AssignmentRepositoryImpl implements AssignmentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 조건에 맞는 과제 요약 DTO를 페이징 조회
     *     <ol>
     *         <li>조건에 맞는 모든 Assignment 공고 id 조회</li>
     *         <li>해당 id에 대한 Assignment 정보 조회</li>
     *     </ol>
     */
    @Override
    public Page<AssignmentSummaryResponseDto> getAssignmentSummaries(
            Long viewerId,
            AssignmentSearchCondition condition,
            Pageable pageable
    ) {
        BooleanBuilder where = new BooleanBuilder()
                .and(eqStatus(condition.status()))
                .and(containsAnyKeyword(condition.keywords()))
                .and(containsAnyGrade(condition.grades()))
                .and(assignment.user.university.eq(  // 같은 대학의 공고만을 조회
                        JPAExpressions
                                .select(user.university)
                                .from(user)
                                .where(user.id.eq(viewerId))
                ));

        // 조건 에 맞는 모든 Assignment 공고 id 조회
        List<Long> assignmentIds = queryFactory
                .select(assignment.id)
                .from(assignment)
                .where(where)
                .orderBy(assignment.createdAt.desc())
                .offset(pageable.getOffset())   // page
                .limit(pageable.getPageSize())  // size
                .fetch();

        // 해당 id의 Assignment 공고 조회
        List<AssignmentSummaryResponseDto> content = getAssignmentSummariesByIds(viewerId, assignmentIds);

        // 조건 만족하는 모든 과제 엔티티 개수를 구하는 쿼리
        // 페이지 총 개수를 제공하기 위함
        JPAQuery<Long> countQuery = queryFactory
                .select(assignment.count())
                .from(assignment)
                .where(where);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 주어진 Assignment ID 목록에 해당하는 과제 요약 정보를 조회한다.
     *
     * @param assignmentIds 조회할 Assignment ID 목록
     * @return 입력 순서에 맞춘 {@code List<AssignmentSummaryResponseDto>}
     */
    @Override
    public List<AssignmentSummaryResponseDto> getAssignmentSummariesByIds(
            Long viewerId,
            List<Long> assignmentIds
    ) {
        // 해당 id에 대한 Assignment 정보 조회
        // collection 데이터를 group by로 묶어서 가져오기 위함
        Map<Long, AssignmentSummaryResponseDto> responseDtoMap = queryFactory
                .from(assignment)
                .innerJoin(assignment.user, user)
                .leftJoin(bookmark)
                .on(
                        bookmark.user.id.eq(viewerId)
                                .and(bookmark.recruitment.id.eq(assignment.id))
                )
                .where(assignment.id.in(assignmentIds))
                .transform(
                        groupBy(assignment.id).as(Projections.constructor(
                                AssignmentSummaryResponseDto.class,
                                assignment.id,
                                assignment.user.id,
                                assignment.user.nickname,
                                // TODO profileImg
                                assignment.user.university,
                                assignment.category,
                                assignment.title,
                                assignment.deadline,
                                assignment.status,
                                assignment.department,
                                assignment.lecture,
                                assignment.lectureCode,
                                set(assignment.grades.any()),
                                bookmark.id.isNotNull(),
                                bookmark.id
                        ))
                );
        // 최종 응답 데이터 (불변 리스트)
        return assignmentIds.stream()
                .map(responseDtoMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Assignment의 상태(status)와 일치하는 조건 생성, 단 CANCELED는 제외
     */
    private BooleanExpression eqStatus(RecruitmentStatus status) {
        if (status == null || status == RecruitmentStatus.CANCELED)
            return assignment.status.ne(RecruitmentStatus.CANCELED);
        return assignment.status.eq(status);
    }

    /**
     * 제목(title) 또는 내용(content)에 키워드 중 하나라도 포함되는 조건 생성
     */
    private BooleanExpression containsAnyKeyword(Set<String> keywords) {
        return RecruitmentSearchMatcher.match(
                keywords,
                assignment.title,
                assignment.content
        );
    }

    /**
     * Project의 grades 컬렉션 중 하나라도 지정된 grades에 포함되는 조건 생성
     */
    private BooleanExpression containsAnyGrade(Set<Integer> grades) {
        if (grades == null || grades.isEmpty())
            return null;
        return assignment.grades.any().in(grades);
    }
}
