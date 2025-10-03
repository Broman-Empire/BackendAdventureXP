package org.example.adventurexp.controller;

import org.springframework.web.bind.annotation.*;

private final IActivityService activityService;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    public String getAllActivities() {
        return activityService.findAll();
    }

}
