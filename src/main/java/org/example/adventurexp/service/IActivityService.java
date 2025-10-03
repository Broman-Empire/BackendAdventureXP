package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;

import java.util.List;

public interface IActivityService {

    List<Activity> findAll();  
  
    Activity createActivity(Activity activity);

    Activity updateActivity(Long id, Activity activity);

}