package com.wagglex2.waggle.domain.bookmark.service;

public interface BookmarkService {

    Long createBookmark(Long userId, Long recruitmentId);
    void deleteBookmark(Long userId, Long bookmarkId);
}
