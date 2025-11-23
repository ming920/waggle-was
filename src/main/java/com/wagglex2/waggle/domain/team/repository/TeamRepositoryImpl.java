package com.wagglex2.waggle.domain.team.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
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
import java.util.Map;
import java.util.stream.Collectors;

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

        // Team ID만 조회
        List<Long> teamIds = queryFactory
                .select(team.id)
                .from(team)
                .where(builder)
                .orderBy(getOrderSpecifiers(pageable))
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

        // 순서 보장
        Map<Long, Team> teamMap = teams.stream()
                .collect(Collectors.toMap(Team::getId, t -> t));
        List<Team> orderedTeams = teamIds.stream()
                .map(teamMap::get)
                .toList();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(team.countDistinct())
                .from(team)
                .where(builder);

        return PageableExecutionUtils.getPage(
                orderedTeams,
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

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{team.createdAt.desc()};
        }

        return pageable.getSort().stream()
                .map(order -> {
                    Path<?> path = getPath(order.getProperty());
                    return order.isAscending()
                            ? new OrderSpecifier(Order.ASC, path)
                            : new OrderSpecifier(Order.DESC, path);
                })
                .toArray(OrderSpecifier[]::new);
    }

    private Path<?> getPath(String property) {
        return switch (property) {
            case "createdAt" -> team.createdAt;
            default -> team.createdAt;
        };
    }

    private BooleanExpression eqCategory(RecruitmentCategory category) {
        return team.recruitment.category.eq(category);
    }

    private BooleanExpression eqStatus(RecruitmentStatus status) {
        return team.recruitment.status.eq(status);
    }

    private BooleanExpression eqUser(Long viewerId) {
        return team.members.any().user.id.eq(viewerId);
    }
}
