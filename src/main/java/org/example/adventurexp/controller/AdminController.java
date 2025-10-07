package org.example.adventurexp.controller;

import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.dto.UpdateReservationRequest;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Reservation;
import org.example.adventurexp.service.IActivityService;
import org.example.adventurexp.service.IReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final IReservationService reservationService;
    private final IActivityService activityService;

    public AdminController (IReservationService reservationService, IActivityService activityService) {
        this.reservationService = reservationService;
        this.activityService = activityService;
    }

    // --- Activity CRUD ---

    // GET /api/admin/activities
    @GetMapping("/activities")
    public List<ActivityDTO> listActivities() {
        return activityService.findAll();
    }

    // POST /api/admin/activities
    @PostMapping("/activities")
    public Activity createActivity(@RequestBody Activity activity) {
        return activityService.createActivity(activity);
    }

    // PATCH /api/admin/activities/{id}
    @PatchMapping("/activities/{id}")
    public Activity updateActivity(@PathVariable("id") Long id, @RequestBody Activity patch) {
        return activityService.updateActivity(id, patch);
    }

    // DELETE /api/admin/activities/{id}
    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable("id") Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/schedule")
    public List<Reservation> getDailySchedule(@RequestParam("date")LocalDate date) {
        return reservationService.getDaySchedule(date);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reservations/{id}")
    public Reservation updateReservation(@PathVariable("id") Long id, @RequestBody UpdateReservationRequest updateRequest) {
        updateRequest.setReservationId(id);
        return reservationService.updateReservation(updateRequest);
    }

}
