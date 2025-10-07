// src/main/java/org/example/adventurexp/mapper/ActivityMapper.java
package org.example.adventurexp.mapper;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.dto.ActivityDTO;

public class ActivityMapper {

    public static ActivityDTO toDTO(Activity activity) {
        if (activity == null) return null;

        return new ActivityDTO(
                activity.getId(),
                activity.getName(),
                activity.getMinAge(),
                activity.getMinParticipants(),
                activity.getMaxParticipants(),
                activity.getDurationMinutes(),
                activity.getParallelCourts()
        );
    }

    public static Activity toEntity(ActivityDTO dto) {
        if (dto == null) return null;

        Activity activity = new Activity();
        activity.setId(dto.getId());
        activity.setName(dto.getName());
        activity.setMinAge(dto.getMinAge());
        activity.setMinParticipants(dto.getMinParticipants());
        activity.setMaxParticipants(dto.getMaxParticipants());
        activity.setDurationMinutes(dto.getDurationMinutes());
        activity.setParallelCourts(dto.getParallelCourts());

        return activity;
    }
}
