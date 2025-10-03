package org.example.adventurexp.controller;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.service.IActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final IActivityService activityService;

    public ActivityController(IActivityService activityService) {
        this.activityService = activityService;
    }

    public List<Activity> getAllActivities() {
        return activityService.findAll();
    }

}
