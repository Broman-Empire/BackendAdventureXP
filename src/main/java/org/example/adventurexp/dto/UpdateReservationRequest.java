package org.example.adventurexp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationRequest {

    private Long reservationId;
    private Long activityId;
    private LocalDateTime newStart;
    private Integer newParticipants;
    private String contactName;
    private String email;
    private String phone; // valgfri

}
