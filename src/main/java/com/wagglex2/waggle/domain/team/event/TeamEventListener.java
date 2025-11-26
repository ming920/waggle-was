package com.wagglex2.waggle.domain.team.event;

import com.wagglex2.waggle.domain.common.event.SimpleRecruitmentCreatedEvent;
import com.wagglex2.waggle.domain.common.event.RecruitmentDeletedEvent;
import com.wagglex2.waggle.domain.project.event.ProjectCreatedEvent;
import com.wagglex2.waggle.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TeamEventListener {

    private final TeamService teamService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleSimpleRecruitmentCreated(SimpleRecruitmentCreatedEvent event) {
        teamService.createByRecruitmentId(event.authorId(), event.recruitmentId());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleProjectCreated(ProjectCreatedEvent event) {
        teamService.createByRecruitmentId(event.authorId(), event.projectId(), event.authorPosition());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRecruitmentDeleted(RecruitmentDeletedEvent event) {
        teamService.deleteByRecruitmentId(event.recruitmentId());
    }
}
