package com.wagglex2.waggle.domain.notification.repository;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Notification n WHERE n.receiver.id = :receiverId")
    void deleteAllByReceiverId(@Param("receiverId") Long receiverId);

    @Modifying(clearAutomatically = true)
    @Query("""
        DELETE FROM Notification n
        WHERE n.receiver.id = :receiverId
        AND n.application.recruitment.category = :category
    """)
    void deleteAllByReceiverIdAndCategory(
            @Param("receiverId")Long receiverId,
            @Param("category") RecruitmentCategory category
    );
}
