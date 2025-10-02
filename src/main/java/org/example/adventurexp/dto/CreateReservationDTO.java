package org.example.adventurexp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateReservationDTO {
    private String customerType, contactName, email, phone;
    private Long activityId;
    private LocalDateTime startsAt;
    private int participants;
}
