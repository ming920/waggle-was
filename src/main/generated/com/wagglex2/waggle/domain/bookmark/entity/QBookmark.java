package com.wagglex2.waggle.domain.bookmark.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBookmark is a Querydsl query type for Bookmark
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBookmark extends EntityPathBase<Bookmark> {

    private static final long serialVersionUID = -1043592980L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBookmark bookmark = new QBookmark("bookmark");

    public final DateTimePath<java.time.LocalDateTime> bookmarkedAt = createDateTime("bookmarkedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.wagglex2.waggle.domain.common.entity.QBaseRecruitment recruitment;

    public final com.wagglex2.waggle.domain.user.entity.QUser user;

    public QBookmark(String variable) {
        this(Bookmark.class, forVariable(variable), INITS);
    }

    public QBookmark(Path<? extends Bookmark> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBookmark(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBookmark(PathMetadata metadata, PathInits inits) {
        this(Bookmark.class, metadata, inits);
    }

    public QBookmark(Class<? extends Bookmark> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recruitment = inits.isInitialized("recruitment") ? new com.wagglex2.waggle.domain.common.entity.QBaseRecruitment(forProperty("recruitment"), inits.get("recruitment")) : null;
        this.user = inits.isInitialized("user") ? new com.wagglex2.waggle.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

