package org.example.adventurexp.service;

import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.repository.IActivityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityService implements IActivityService {

    IActivityRepository activityRepository;

    public ActivityService(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
  
    @Override
    public List<ActivityDTO> findAll() {
        return activityRepository.findAll().stream()
                .map(activity -> new ActivityDTO(
                        activity.getId(),
                        activity.getName(),
                        activity.getMinAge(),
                        activity.getDurationMinutes()
                ))
                .toList();
    }

    @Override
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Long id, Activity activity) {
        Activity toBeUpdated = activityRepository.findById(id).orElseThrow(); // Smider exception hvis ikke id findes. Så undgår jeg at skulle bruge Optional
        toBeUpdated.setName(activity.getName());
        toBeUpdated.setMinAge(activity.getMinAge());
        toBeUpdated.setMinParticipants(activity.getMinParticipants());
        toBeUpdated.setMaxParticipants(activity.getMaxParticipants());
        toBeUpdated.setDurationMinutes(activity.getDurationMinutes());
        toBeUpdated.setParallelCourts(activity.getParallelCourts());
        return activityRepository.save(toBeUpdated);
    }
}
