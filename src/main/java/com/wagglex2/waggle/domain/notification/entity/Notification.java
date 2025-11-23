package com.wagglex2.waggle.domain.notification.entity;

import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.notification.type.NotificationType;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "notifications")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false, updatable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id", nullable = false, updatable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Application application;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    public Notification(User sender, User receiver, Application application, NotificationType type) {
        this.sender = sender;
        this.receiver = receiver;
        this.application = application;
        this.type = type;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
