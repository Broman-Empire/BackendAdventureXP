package org.example.adventurexp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ActivityDTO {

    private Long id;
    private String name;
    private int minAge;
    private double durationMinutes;
}
