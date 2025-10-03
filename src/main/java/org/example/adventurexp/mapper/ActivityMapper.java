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
                activity.getDurationMinutes()
        );
    }
}
