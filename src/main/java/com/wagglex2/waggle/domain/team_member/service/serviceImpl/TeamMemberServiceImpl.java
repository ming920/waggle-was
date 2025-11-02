package com.wagglex2.waggle.domain.team_member.service.serviceImpl;

import com.wagglex2.waggle.domain.team_member.repository.TeamMemberRepository;
import com.wagglex2.waggle.domain.team_member.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

}
