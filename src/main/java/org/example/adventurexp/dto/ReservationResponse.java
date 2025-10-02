package org.example.adventurexp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {

    private Long id, activityId, participants, totalParticipants;
    private LocalDateTime startsAt;
}
