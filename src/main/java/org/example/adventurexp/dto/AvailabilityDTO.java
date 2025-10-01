package org.example.adventurexp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AvailabilityDTO {

    private LocalDateTime start;
    private LocalDateTime end;
    private int capacity;
    private int remaining;
    private boolean soldOut;

}
