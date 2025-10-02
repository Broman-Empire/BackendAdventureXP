package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.repository.IActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class ActivityService implements IActivityService {

    IActivityRepository activityRepository;

    public ActivityService(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }
}
