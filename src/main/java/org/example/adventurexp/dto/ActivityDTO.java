package org.example.adventurexp.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ActivityDTO {

    private Long id;
    private String name;
    private int minAge;
    private int durationMinutes;
}
