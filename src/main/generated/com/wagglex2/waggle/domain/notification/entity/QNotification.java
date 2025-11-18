package com.wagglex2.waggle.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = 1513606998L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final com.wagglex2.waggle.domain.application.entity.QApplication application;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isRead = createBoolean("isRead");

    public final com.wagglex2.waggle.domain.user.entity.QUser receiver;

    public final com.wagglex2.waggle.domain.user.entity.QUser sender;

    public final EnumPath<com.wagglex2.waggle.domain.notification.type.NotificationType> type = createEnum("type", com.wagglex2.waggle.domain.notification.type.NotificationType.class);

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.application = inits.isInitialized("application") ? new com.wagglex2.waggle.domain.application.entity.QApplication(forProperty("application"), inits.get("application")) : null;
        this.receiver = inits.isInitialized("receiver") ? new com.wagglex2.waggle.domain.user.entity.QUser(forProperty("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new com.wagglex2.waggle.domain.user.entity.QUser(forProperty("sender")) : null;
    }

}

