package org.example.adventurexp.dto;


import lombok.*;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateReservationDTO {
    private String customerType, contactName, email, phone;
    private Long activityId;
    private LocalDateTime startsAt;
    private int participants;
    private int groupMinAge;
    private Long slotId;
}
