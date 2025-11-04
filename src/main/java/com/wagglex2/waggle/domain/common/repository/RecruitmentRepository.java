package com.wagglex2.waggle.domain.common.repository;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RecruitmentRepository extends JpaRepository<BaseRecruitment, Long> {

    @Query("""
        SELECT r FROM BaseRecruitment r
        WHERE r.id = :id
        AND r.status != com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CANCELED
    """)
    Optional<BaseRecruitment> findByIdNotCanceled(@Param("id") Long id);

    /**
     * 마감일(deadline)이 기준 시각(baseTime) 이전인 공고들의 상태를 CLOSED로 변경
     * <p>
     * 기준 시각은 자정(00:00)으로 설정
     *
     * @param baseTime 기준 시각
     * @return 업데이트된 공고 건수
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE BaseRecruitment r
        SET r.status = com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CLOSED
        WHERE r.deadline < :baseTime
    """)
    int closeExpiredRecruitments(@Param("baseTime") LocalDateTime baseTime);

    /**
     * 모집글(BaseRecruitment) ID로 조회하며, 취소되지 않은(CANCELED 아님) 모집글만 반환한다.
     * <p>
     * - PESSIMISTIC_WRITE 락을 사용하여 해당 모집글 레코드를 갱신 중인 다른 트랜잭션이 접근하지 못하도록 막는다.<br>
     * - 조회 시점에 이미 취소된 모집글은 반환하지 않는다.
     * </p>
     *
     * @param id 조회할 모집글 ID
     * @return 취소되지 않은 모집글 (Optional)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r FROM BaseRecruitment r
        WHERE r.id = :id
        AND r.status != com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CANCELED
    """)
    Optional<BaseRecruitment> findByIdNotCanceledForUpdate(@Param("id") Long id);
}
