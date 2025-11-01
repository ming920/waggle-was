package com.wagglex2.waggle.domain.application.repository;

import com.wagglex2.waggle.domain.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByApplicantIdAndRecruitmentId(Long applicantId, Long recruitmentId);
}
