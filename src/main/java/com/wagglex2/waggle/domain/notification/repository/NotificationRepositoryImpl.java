package com.wagglex2.waggle.domain.notification.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.notification.dto.response.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wagglex2.waggle.domain.application.entity.QApplication.application;
import static com.wagglex2.waggle.domain.common.entity.QBaseRecruitment.baseRecruitment;
import static com.wagglex2.waggle.domain.notification.entity.QNotification.*;
import static com.wagglex2.waggle.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NotificationResponseDto> getAllByUserIdAndCategory(Long receiverId, RecruitmentCategory category, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(notification.receiver.id.eq(receiverId));

        if (category != null) {
            builder.and(notification.application.recruitment.category.eq(category));
        }

        JPAQuery<NotificationResponseDto> query = queryFactory
                .select(Projections.constructor(
                                NotificationResponseDto.class,
                                notification.id,
                                notification.application.id,
                                notification.application.recruitment.category,
                                notification.sender.nickname,
                                notification.type,
                                notification.createdAt,
                                notification.isRead
                        )
                )
                .from(notification)
                .join(notification.sender, user)
                .join(notification.application, application)
                .join(application.recruitment, baseRecruitment)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // Sort -> OrderSpecifier 변환
        for (Sort.Order order : pageable.getSort()) {
            PathBuilder<?> pathBuilder = new PathBuilder<>(notification.getType(), notification.getMetadata());
            query.orderBy(new OrderSpecifier<>(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(order.getProperty(), Comparable.class)
            ));
        }

        List<NotificationResponseDto> content = query.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notification.count())
                .from(notification)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
