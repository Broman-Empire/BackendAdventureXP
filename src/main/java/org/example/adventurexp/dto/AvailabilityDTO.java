package org.example.adventurexp.dto;


import lombok.*;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class AvailabilityDTO {
    private LocalDateTime start;
    private LocalDateTime end;
    private int capacity;
    private int remaining;
    private Long slotId;
    private boolean soldOut;
    
}
