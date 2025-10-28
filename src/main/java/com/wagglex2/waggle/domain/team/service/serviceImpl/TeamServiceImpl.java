package com.wagglex2.waggle.domain.team.service.serviceImpl;

import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team.repository.TeamRepository;
import com.wagglex2.waggle.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public void save(Team team) {
        teamRepository.save(team);
    }
}
