package org.example.adventurexp.service;

import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.model.Activity;

import java.util.List;

public interface IActivityService {

    List<ActivityDTO> findAll();
  
    Activity createActivity(Activity activity);

    Activity updateActivity(Long id, Activity activity);

    Activity deleteActivity(Long id);

}