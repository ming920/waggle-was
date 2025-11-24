package com.wagglex2.waggle.domain.team.event;

import com.wagglex2.waggle.domain.common.event.CreateRecruitmentEvent;
import com.wagglex2.waggle.domain.common.event.RecruitmentDeletedEvent;
import com.wagglex2.waggle.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TeamEventListener {

    private final TeamService teamService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createTeam(CreateRecruitmentEvent event) {
        teamService.createByRecruitmentId(event.userId(), event.recruitmentId());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRecruitmentDeleted(RecruitmentDeletedEvent event) {
        teamService.deleteByRecruitmentId(event.recruitmentId());
    }
}
