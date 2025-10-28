package com.wagglex2.waggle.domain.team_member.entity.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <h2>TeamRole (팀 내 역할)</h2>
 *
 * <ul>
 *   <li><b>LEADER</b> – 팀 생성자 또는 리더</li>
 *   <li><b>MEMBER</b> – 일반 팀원</li>
 * </ul>
 *
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TeamRole {
    LEADER("리더"),
    MEMBER("멤버");

    private final String desc;
}
