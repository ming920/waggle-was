package com.wagglex2.waggle.domain.assignment.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentSearchCondition;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentSummaryResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.wagglex2.waggle.domain.assignment.entity.QAssignment.assignment;
import static com.wagglex2.waggle.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class AssignmentRepositoryImpl implements AssignmentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 조건에 맞는 과제(Assignment) 요약 DTO 페이징 조회
     * <p>
     * 프로젝트 코드 패턴을 그대로 따라, 2단계 조회
     */
    @Override
    public Page<AssignmentSummaryResponseDto> findAssignmentSummaries(AssignmentSearchCondition condition, Pageable pageable) {
        // 1) 조건에 맞는 Assignment 공고 id 페이지 조회
        List<Long> assignmentIds = queryFactory
                .select(assignment.id)
                .from(assignment)
                .where(
                        eqStatus(condition.status()),
                        containsAnyKeyword(condition.keywords()),
                        containsAnyGrade(condition.grades())
                )
                .offset(pageable.getOffset())          // page
                .limit(pageable.getPageSize())         // size
                .orderBy(assignment.createdAt.desc())
                .fetch();

        if (assignmentIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 해당 id에 대한 Assignment 정보 조회
        Map<Long, AssignmentSummaryResponseDto> responseDtoMap = queryFactory
                .from(assignment)
                .innerJoin(assignment.user, user)
                .where(assignment.id.in(assignmentIds))
                .transform(
                        groupBy(assignment.id).as(Projections.constructor(
                                AssignmentSummaryResponseDto.class,
                                assignment.id,
                                assignment.user.id,
                                assignment.user.nickname,
                                // TODO: profileImg
                                assignment.user.university,
                                assignment.category,
                                assignment.title,
                                assignment.deadline,
                                assignment.status,
                                set(assignment.grades)
                        ))
                );

        // 최종 응답 데이터(요청한 페이지 순서 보존)
        List<AssignmentSummaryResponseDto> content = assignmentIds.stream()
                .map(responseDtoMap::get)
                .toList();

        // 총 개수(count) 조회 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(assignment.count())
                .from(assignment)
                .where(
                        eqStatus(condition.status()),
                        containsAnyKeyword(condition.keywords()),
                        containsAnyGrade(condition.grades())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * Assignment의 상태(status) 일치 조건(CANCELED는 제외)
     */
    private BooleanExpression eqStatus(RecruitmentStatus status) {
        if (status == null || status == RecruitmentStatus.CANCELED) {
            return null;
        }
        return assignment.status.eq(status);
    }

    /**
     * 제목 또는 내용에 키워드 중 하나라도 포함
     */
    private BooleanBuilder containsAnyKeyword(Set<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return null;
        }
        BooleanBuilder builder = new BooleanBuilder();
        keywords.forEach(keyword ->
                builder
                        .or(assignment.title.containsIgnoreCase(keyword))
                        .or(assignment.content.containsIgnoreCase(keyword))
        );
        return builder;
    }

    /**
     * grade IN (...)
     * */
    private BooleanExpression containsAnyGrade(Set<Integer> grades) {
        if (grades == null || grades.isEmpty()) return null;
        return assignment.grades.any().in(grades);
    }
}
