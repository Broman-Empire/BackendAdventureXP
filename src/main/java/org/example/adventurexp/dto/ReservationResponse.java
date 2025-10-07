package org.example.adventurexp.dto;

import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ReservationResponse {

    private Long id, activityId, participants, totalParticipants;
    private LocalDateTime startsAt;
}
