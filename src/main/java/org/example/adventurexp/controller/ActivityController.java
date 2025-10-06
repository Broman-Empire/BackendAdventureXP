package org.example.adventurexp.controller;

import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.dto.AvailabilityDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.service.IActivityService;
import org.example.adventurexp.service.IAvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class ActivityController {

    private final IActivityService activityService;
    private final IAvailabilityService availabilityService;

    public ActivityController(IActivityService activityService, IAvailabilityService availabilityService) {
        this.activityService = activityService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/activities")
    public List<ActivityDTO> getAllActivities() {
        return activityService.findAll();
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityDTO[]> getAvailability(@RequestParam("activityId") long activityId,
                                                             @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime openTime,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime closeTime) {

        return ResponseEntity.ok(
                availabilityService.getDailyAvailability(activityId, fromDate, toDate, openTime, closeTime)
        );


    }
}