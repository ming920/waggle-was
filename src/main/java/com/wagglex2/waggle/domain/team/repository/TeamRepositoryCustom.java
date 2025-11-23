package com.wagglex2.waggle.domain.team.repository;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Team 엔티티 조회를 위한 커스텀 Repository 인터페이스.
 * 페이징, 조건 검색, N+1 방지를 위한 fetch 전용 메서드를 포함한다.
 */
public interface TeamRepositoryCustom {

    /**
     * 로그인한 사용자가 속한 팀 목록을 조건별로 조회한다.
     *
     * <p>쿼리 구조는 아래와 같다:</p>
     * <ul>
     *     <li>1) Team ID만 먼저 페이징하여 조회</li>
     *     <li>2) 조회된 ID 목록으로 Team + Recruitment + User 를 fetch join하여 한 번에 로딩</li>
     *     <li>3) 별도 쿼리로 TeamMember + User 를 fetch join 하여 N+1 문제 방지</li>
     * </ul>
     *
     * @param viewerId 조회하는 사용자 ID (팀장 또는 팀원)
     * @param category 공고 카테고리 필터
     * @param status 공고 상태 필터
     * @param pageable 페이징 및 정렬 정보
     * @return 조건에 맞는 Team 목록을 Page 형태로 반환
     */
    Page<Team> getTeams(Long viewerId, RecruitmentCategory category, RecruitmentStatus status, Pageable pageable);

    /**
     * TeamMember와 연결된 User를 fetch join하여
     * 지정된 팀 목록의 멤버 데이터를 한 번에 로딩한다.
     *
     * <p>Team 조회 시 Lazy loading으로 인해 발생할 수 있는
     * N+1 문제를 예방하기 위해 사용된다.</p>
     *
     * @param teamIds 멤버를 미리 로딩할 팀 ID 목록
     */
    void fetchMembers(List<Long> teamIds);
}
