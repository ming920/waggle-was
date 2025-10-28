package com.wagglex2.waggle.domain.common.repository;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitmentRepository extends JpaRepository<BaseRecruitment, Long> {

    @Query("""
        SELECT r FROM BaseRecruitment r
        WHERE r.id = :id
        AND r.status != com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CANCELED
    """)
    Optional<BaseRecruitment> findByIdNotCanceled(@Param("id") Long id);
}
