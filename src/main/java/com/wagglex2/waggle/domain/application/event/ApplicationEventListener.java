package com.wagglex2.waggle.domain.application.event;

import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.common.event.RecruitmentReopenedEvent;
import com.wagglex2.waggle.domain.common.event.RecruitmentDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ApplicationEventListener {

    private final ApplicationService applicationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecruitmentDeleted(RecruitmentDeletedEvent event) {
        applicationService.cancelApplication(event.recruitmentId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecruitmentReopened(RecruitmentReopenedEvent event) {
        applicationService.updateAllByRecruitmentReopened(event.recruitmentId());
    }
}
