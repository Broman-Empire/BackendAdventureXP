package org.example.adventurexp.dto;


import lombok.*;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationRequest {

    private Long activityId;
    private LocalDateTime newStart;
    private Integer newParticipants;
    private String contactName;
    private String email;
    private String phone; // valgfri
    private String customerType; // valgfri

}
