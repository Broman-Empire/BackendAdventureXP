package org.example.adventurexp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingScheduleDTO {
    private Long bookingId;
    private Long reservationId;
    private String activityName;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private int participants;
    private int totalParticipants; // capacity for the slot
    private String contactName;
    private String customerType;
}