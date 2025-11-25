package com.wagglex2.waggle.domain.study.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wagglex2.waggle.domain.common.querydsl.RecruitmentSearchMatcher;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.study.dto.request.StudySearchCondition;
import com.wagglex2.waggle.domain.study.dto.response.StudySummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.wagglex2.waggle.domain.bookmark.entity.QBookmark.bookmark;
import static com.wagglex2.waggle.domain.study.entity.QStudy.study;
import static com.wagglex2.waggle.domain.user.entity.QUser.user;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * StudyRepositoryCustom 인터페이스의 구현체
 */
@Repository
@RequiredArgsConstructor
public class StudyRepositoryImpl implements StudyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 조건에 맞는 스터디(Study) 요약 DTO 페이징 조회
     */
    @Override
    public Page<StudySummaryResponseDto> getStudySummaries(Long viewerId, StudySearchCondition condition, Pageable pageable) {
        // where문 조건
        BooleanBuilder builder = new BooleanBuilder()
                .and(eqStatus(condition.status()))
                .and(containsAnyKeyword(condition.keywords()))
                .and(containsAnySkill(condition.skills()))
                .and(study.user.university.eq(  // 같은 대학의 공고만을 조회
                        JPAExpressions
                                .select(user.university)
                                .from(user)
                                .where(user.id.eq(viewerId))
                ));

        // 조건에 맞는 모든 Study 공고 id 조회
        List<Long> studyIds = queryFactory
                .select(study.id)
                .from(study)
                .where(builder)
                .offset(pageable.getOffset())  // page
                .limit(pageable.getPageSize()) // size
                .orderBy(study.createdAt.desc())
                .fetch();

        // 해당 id의 Study 공고 조회
        List<StudySummaryResponseDto> content = getStudySummariesByIds(viewerId, studyIds);

        // 조건을 만족하는 모든 스터디 엔티티의 개수를 구하는 쿼리
        // 페이지의 총 개수를 제공하기 위함
        JPAQuery<Long> countQuery = queryFactory
                .select(study.count())
                .from(study)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 주어진 Study ID 목록에 해당하는 스터디 요약 정보를 조회한다.
     *
     * @param studyIds 조회할 Study ID 목록
     * @return 입력 순서에 맞춘 {@code List<StudySummaryResponseDto>}
     */
    @Override
    public List<StudySummaryResponseDto> getStudySummariesByIds(
                Long viewerId,
                List<Long> studyIds
) {
        // 해당 id에 대한 Study 정보 조회
        // collection 데이터를 group by로 묶어서 가져오기 위함
        Map<Long, StudySummaryResponseDto> responseDtoMap = queryFactory
                .from(study)
                .innerJoin(study.user, user)
                .leftJoin(bookmark)
                .on(
                        bookmark.user.id.eq(viewerId)
                                .and(bookmark.recruitment.id.eq(study.id))
                )
                .where(study.id.in(studyIds))
                .transform(
                        groupBy(study.id).as(Projections.constructor(
                                StudySummaryResponseDto.class,
                                study.id,
                                study.user.id,
                                study.user.nickname,
                                // TODO profileImg
                                study.user.university,
                                study.category,
                                study.title,
                                study.deadline,
                                study.status,
                                set(study.skills),
                                bookmark.id.isNotNull(),
                                bookmark.id
                        ))
                );

        // 최종 응답 데이터 (불변 리스트)
        return studyIds.stream()
                .filter(Objects::nonNull)
                .map(responseDtoMap::get)
                .toList();
    }

    /**
     * Study의 상태(status)와 일치하는 조건 생성, 단 CANCELED는 제외
     */
    private BooleanExpression eqStatus(RecruitmentStatus status) {
        if (status == null || status == RecruitmentStatus.CANCELED) {
            return study.status.ne(RecruitmentStatus.CANCELED);
        }

        return study.status.eq(status);
    }

    /**
     * 제목(title) 또는 내용(content)에 키워드 중 하나라도 포함되는 조건 생성
     */
    private BooleanExpression containsAnyKeyword(Set<String> keywords) {
        return RecruitmentSearchMatcher.match(
                keywords,
                study.title,
                study.content
        );
    }

    /**
     * Study의 skills 컬렉션 중 하나라도 지정된 skills에 포함되는 조건 생성
     */
    private BooleanExpression containsAnySkill(Set<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return null;
        }

        return study.skills.any().in(skills);
    }
}
