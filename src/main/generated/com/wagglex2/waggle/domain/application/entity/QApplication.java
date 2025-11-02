package com.wagglex2.waggle.domain.application.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApplication is a Querydsl query type for Application
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApplication extends EntityPathBase<Application> {

    private static final long serialVersionUID = 1795889510L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApplication application = new QApplication("application");

    public final com.wagglex2.waggle.domain.user.entity.QUser applicant;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> grade = createNumber("grade", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final EnumPath<com.wagglex2.waggle.domain.project.type.MeetingType> meetingType = createEnum("meetingType", com.wagglex2.waggle.domain.project.type.MeetingType.class);

    public final EnumPath<com.wagglex2.waggle.domain.common.type.PositionType> position = createEnum("position", com.wagglex2.waggle.domain.common.type.PositionType.class);

    public final com.wagglex2.waggle.domain.common.entity.QBaseRecruitment recruitment;

    public final SetPath<com.wagglex2.waggle.domain.common.type.Skill, EnumPath<com.wagglex2.waggle.domain.common.type.Skill>> skills = this.<com.wagglex2.waggle.domain.common.type.Skill, EnumPath<com.wagglex2.waggle.domain.common.type.Skill>>createSet("skills", com.wagglex2.waggle.domain.common.type.Skill.class, EnumPath.class, PathInits.DIRECT2);

    public final EnumPath<com.wagglex2.waggle.domain.application.type.ApplicationStatus> status = createEnum("status", com.wagglex2.waggle.domain.application.type.ApplicationStatus.class);

    public QApplication(String variable) {
        this(Application.class, forVariable(variable), INITS);
    }

    public QApplication(Path<? extends Application> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApplication(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApplication(PathMetadata metadata, PathInits inits) {
        this(Application.class, metadata, inits);
    }

    public QApplication(Class<? extends Application> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.applicant = inits.isInitialized("applicant") ? new com.wagglex2.waggle.domain.user.entity.QUser(forProperty("applicant")) : null;
        this.recruitment = inits.isInitialized("recruitment") ? new com.wagglex2.waggle.domain.common.entity.QBaseRecruitment(forProperty("recruitment"), inits.get("recruitment")) : null;
    }

}

