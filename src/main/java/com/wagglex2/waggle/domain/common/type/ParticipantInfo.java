package com.wagglex2.waggle.domain.common.type;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.dto.request.PositionInfoCreationRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;


/**
 * 모집 인원 현황을 나타내는 VO 클래스
 * <p>
 * <ul>
 *   <li>maxParticipants: 최대 모집 인원</li>
 *   <li>currParticipants: 현재 모집된 인원</li>
 * </ul>
 * <p>
 * {@link PositionType} 정보가 필요 없는 Assignment와 Study에서 사용된다.
 *
 * @see PositionParticipantInfo
 * @see PositionInfoCreationRequestDto
 * @author 오재민
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
@Embeddable
public class ParticipantInfo {
    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Column(name = "curr_participants", nullable = false)
    private int currParticipants = 0;

    public ParticipantInfo(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public boolean isFull() {
        return this.currParticipants >= this.maxParticipants;
    }

    /**
     * 현재 참여 인원을 1명 감소시킨다.
     * <p>
     * - 모집 인원 관리 로직의 핵심 메서드로, 팀원 강퇴 시 호출된다.<br>
     * - 현재 인원(`currParticipants`)이 0 이하일 경우, 더 이상 감소할 수 없으므로 예외를 발생시킨다.<br>
     * </p>
     */
    public void decreaseCurrParticipants() {
        if (this.currParticipants <= 0) {
            throw new BusinessException(ErrorCode.INVALID_MEMBER_COUNT);
        }

        this.currParticipants--;
    }
}
