package com.wagglex2.waggle.domain.bookmark.entity;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자가 특정 공고(BaseRecruitment)를 찜(Bookmark)한 기록을 나타내는 엔티티.
 * <p>
 * 사용자가 찜한 공고 정보를 저장하며, 이를 통해 사용자의 관심 공고 목록을 관리할 수 있다.
 * </p>
 *
 * <ul>
 *   <li>{@link User} : 찜한 사용자 (N:1 관계)</li>
 *   <li>{@link BaseRecruitment} : 찜한 공고 (N:1 관계)</li>
 *   <li>bookmarkedAt : 찜한 시각</li>
 * </ul>
 *
 * @see User
 * @see BaseRecruitment
 * @author 오재민
 */
@Table(
        name = "bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bookmarks_user_id_recruitment_id",
                        columnNames = {"user_id", "recruitment_id"}
                ),
        }
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", referencedColumnName = "id")
    private BaseRecruitment recruitment;

    @Column(name = "bookmarked_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime bookmarkedAt;

    public Bookmark(User user, BaseRecruitment recruitment) {
        this.user = user;
        this.recruitment = recruitment;
    }
}
