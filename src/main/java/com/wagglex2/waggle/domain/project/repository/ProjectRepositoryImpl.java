package com.wagglex2.waggle.domain.project.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wagglex2.waggle.domain.common.querydsl.RecruitmentSearchMatcher;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.dto.request.ProjectSearchCondition;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.wagglex2.waggle.domain.project.entity.QProject.project;
import static com.wagglex2.waggle.domain.user.entity.QUser.user;

/**
 * ProjectRepositoryCustom 인터페이스의 구현체
 */
@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 조건에 맞는 프로젝트 요약 DTO를 페이징 조회
     * <p>
     * positions, skills와 같은 컬렉션 데이터를 단일 쿼리로 가져올 수 없어, 2단계 조회 수행
     * <p>
     *     <ol>
     *         <li>조건에 맞는 모든 Project 공고 id 조회</li>
     *         <li>해당 id에 대한 Project 정보 조회</li>
     *     </ol>
     */
    @Override
    public Page<ProjectSummaryResponseDto> getProjectSummaries(Long viewerId, ProjectSearchCondition condition, Pageable pageable) {
        // where문 조건
        BooleanBuilder builder = new BooleanBuilder()
                .and(eqPurpose(condition.purpose()))
                .and(eqStatus(condition.status()))
                .and(containsAnyKeyword(condition.keywords()))
                .and(containsAnyPosition(condition.positions()))
                .and(containsAnySkill(condition.skills()))
                .and(project.user.university.eq(  // 같은 대학의 공고만을 조회
                        JPAExpressions
                                .select(user.university)
                                .from(user)
                                .where(user.id.eq(viewerId))
                ));

        // 조건에 맞는 모든 Project 공고 id 조회
        List<Long> projectIds = queryFactory
                .select(project.id)
                .from(project)
                .where(builder)
                .offset(pageable.getOffset())  // page
                .limit(pageable.getPageSize()) // size
                .orderBy(project.createdAt.desc())
                .fetch();

        // 해당 id의 Project 공고 조회
        List<ProjectSummaryResponseDto> content = getProjectSummariesByIds(projectIds);

        // 조건을 만족하는 모든 프로젝트 엔티티의 개수를 구하는 쿼리
        // 페이지의 총 개수를 제공하기 위함
        JPAQuery<Long> countQuery = queryFactory
                .select(project.count())
                .from(project)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 주어진 Project ID 목록에 해당하는 프로젝트 요약 정보를 조회한다.
     *
     * @param projectIds 조회할 Project ID 목록
     * @return 입력 순서에 맞춘 {@code List<ProjectSummaryResponseDto>}
     */
    @Override
    public List<ProjectSummaryResponseDto> getProjectSummariesByIds(List<Long> projectIds) {
        // 해당 id에 대한 Project 정보 조회
        // collection 데이터를 group by로 묶어서 가져오기 위함
        Map<Long, ProjectSummaryResponseDto> responseDtoMap = queryFactory
                .from(project)
                .innerJoin(project.user, user)
                .where(project.id.in(projectIds))
                .transform(
                        groupBy(project.id).as(Projections.constructor(
                                ProjectSummaryResponseDto.class,
                                project.id,
                                project.user.id,
                                project.user.nickname,
                                // TODO profileImg
                                project.user.university,
                                project.category,
                                project.title,
                                project.deadline,
                                project.status,
                                project.purpose,
                                project.meetingType,
                                set(project.positions.any().position),
                                set(project.skills)
                        ))
                );

        // 최종 응답 데이터 (불변 리스트)
        return projectIds.stream()
                .filter(Objects::nonNull)
                .map(responseDtoMap::get)
                .toList();
    }

    /**
     * Project의 목적(purpose)과 일치하는 조건 생성
     */
    private BooleanExpression eqPurpose(ProjectPurpose purpose) {
        if (purpose == null) {
            return null;
        }

        return project.purpose.eq(purpose);
    }

    /**
     * Project의 상태(status)와 일치하는 조건 생성, 단 CANCELED는 제외
     */
    private BooleanExpression eqStatus(RecruitmentStatus status) {
        if (status == null || status == RecruitmentStatus.CANCELED) {
            return project.status.ne(RecruitmentStatus.CANCELED);
        }

        return project.status.eq(status);
    }

    /**
     * 제목(title) 또는 내용(content)에 키워드 중 하나라도 포함되는 조건 생성
     */
    private BooleanExpression containsAnyKeyword(Set<String> keywords) {
        return RecruitmentSearchMatcher.match(
                keywords,
                project.title,
                project.content
        );
    }

    /**
     * Project의 positions 컬렉션 중 하나라도 지정된 positions에 포함되는 조건 생성
     */
    private BooleanExpression containsAnyPosition(Set<PositionType> positions) {
        if (positions == null || positions.isEmpty()) {
            return null;
        }

        return project.positions.any().position.in(positions);
    }

    /**
     * Project의 skills 컬렉션 중 하나라도 지정된 skills에 포함되는 조건 생성
     */
    private BooleanExpression containsAnySkill(Set<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return null;
        }

        return project.skills.any().in(skills);
    }
}
