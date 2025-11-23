package com.wagglex2.waggle.domain.bookmark.event;

import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.common.event.RecruitmentDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BookmarkEventListener {

    private final BookmarkService bookmarkService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecruitmentDeleted(RecruitmentDeletedEvent event) {
        bookmarkService.deleteAllByRecruitmentId(event.recruitmentId());
    }
}
