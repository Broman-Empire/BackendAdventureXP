package org.example.adventurexp.controller;

import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.dto.AvailabilityDTO;
import org.example.adventurexp.service.IActivityService;
import org.example.adventurexp.service.IAvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/actrivities")
public class ActivityController {

    private final IActivityService activityService;
    private final IAvailabilityService availabilityService;

    public ActivityController(IActivityService activityService, IAvailabilityService availabilityService) {
        this.activityService = activityService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/all")
    public List<ActivityDTO> getAllActivities() {
        return activityService.findAll();
    }

    @GetMapping("/availability")
    public AvailabilityDTO[] getAvailability(@RequestParam("activityId") long activityId, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return availabilityService.getDailyAvailability(activityId, date);
    }
}