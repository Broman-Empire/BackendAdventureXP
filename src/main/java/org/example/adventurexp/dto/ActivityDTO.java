package org.example.adventurexp.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ActivityDTO {

    private Long id;
    private String name;
    private int minAge;
    private int minParticipants;
    private int maxParticipants;
    private int durationMinutes;
    private int parallelCourts;
}
