package com.wagglex2.waggle.common.scheduler;

import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.application.type.ApplicationStatus;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 모집 공고 마감 처리 스케줄러
 *
 * <p>매일 0시 0분 0초(자정)에 실행되어, 마감일이 지난 모집 공고를
 * {@link RecruitmentStatus#CLOSED CLOSED(마감)} 상태로 변경하고,<br>
 * 해당 모집 공고에 연결된 지원(Application)의 상태도 {@link ApplicationStatus#CLOSED CLOSED(모집종료)}로 업데이트한다.</p>
 *
 * <p>상세 처리 로직:</p>
 * <ul>
 *     <li>{@link RecruitmentService#closeExpiredRecruitments()} : 모집 공고 상태를 Closed로 변경한다</li>
 *     <li>{@link ApplicationService#closeApplicationsForClosedRecruitments()} : 지원 상태를 Closed로 변경한다</li>
 * </ul>
 *
 * <p>Timezone은 'Asia/Seoul' 기준으로 실행한다.</p>
 */
@Component
@RequiredArgsConstructor
public class RecruitmentClosingScheduler {

    private final RecruitmentService recruitmentService;
    private final ApplicationService applicationService;

    // 매일 자정(00:00:00)에 실행
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void scheduleRecruitmentClosing() {
        recruitmentService.closeExpiredRecruitments();
        applicationService.closeApplicationsForClosedRecruitments();
    }
}
