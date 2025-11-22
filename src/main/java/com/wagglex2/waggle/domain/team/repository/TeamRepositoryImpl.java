package com.wagglex2.waggle.domain.team.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wagglex2.waggle.domain.team.entity.QTeam.team;
import static com.wagglex2.waggle.domain.team_member.entity.QTeamMember.teamMember;
import static com.wagglex2.waggle.domain.common.entity.QBaseRecruitment.baseRecruitment;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Team> getTeams(
            Long viewerId,
            RecruitmentCategory category,
            RecruitmentStatus status,
            Pageable pageable
    ) {

        BooleanBuilder builder = new BooleanBuilder()
                .and(eqCategory(category))
                .and(eqStatus(status))
                .and(eqUser(viewerId));

        // Team ID만 먼저 페이징 조회 (Fetch Join 안함)
        List<Long> teamIds = queryFactory
                .select(team.id)
                .from(team)
                .leftJoin(team.members, teamMember)
                .leftJoin(team.recruitment, baseRecruitment)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (teamIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // ID로 필요한 데이터만 fetch Join
        List<Team> teams = queryFactory
                .selectFrom(team)
                .leftJoin(team.recruitment, baseRecruitment).fetchJoin()
                .leftJoin(baseRecruitment.user).fetchJoin()
                .where(team.id.in(teamIds))
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(team.id.countDistinct())
                .from(team)
                .leftJoin(team.members, teamMember)
                .leftJoin(team.recruitment, baseRecruitment)
                .where(builder);

        return PageableExecutionUtils.getPage(
                teams,
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public void fetchMembers(List<Long> teamIds) {
        queryFactory
                .selectFrom(teamMember)
                .leftJoin(teamMember.user).fetchJoin()
                .where(teamMember.team.id.in(teamIds))
                .fetch();
    }

    private BooleanExpression eqCategory(RecruitmentCategory category) {
        return category != null ? baseRecruitment.category.eq(category) : null;
    }

    private BooleanExpression eqStatus(RecruitmentStatus status) {
        return status != null ? baseRecruitment.status.eq(status) : null;
    }

    private BooleanExpression eqUser(Long viewerId) {
        if (viewerId == null) {
            return null;
        }

        return teamMember.user.id.eq(viewerId)
                .or(baseRecruitment.user.id.eq(viewerId));
    }
}
