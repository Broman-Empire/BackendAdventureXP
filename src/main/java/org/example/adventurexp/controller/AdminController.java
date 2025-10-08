package org.example.adventurexp.controller;

import org.example.adventurexp.dto.ReservationLookupDTO;
import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.dto.UpdateReservationRequest;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.model.Reservation;
import org.example.adventurexp.service.IActivityService;
import org.example.adventurexp.service.IEquipmentService;
import org.example.adventurexp.service.IReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final IReservationService reservationService;
    private final IActivityService activityService;
    private final IEquipmentService equipmentService;

    public AdminController (IReservationService reservationService, IActivityService activityService, IEquipmentService equipmentService) {
        this.reservationService = reservationService;
        this.activityService = activityService;
        this.equipmentService = equipmentService;
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

    // --- Equipment CRUD ---

    // GET /api/admin/activities/{id}/equipment
    @GetMapping("/activities/{id}/equipment")
    public List<Equipment> listByActivity(@PathVariable("id") Long activityId) {
        return equipmentService.listByActivity(activityId);
    }

    // PATCH /api/admin/equipment/{id}
    @PatchMapping("/equipment/{id}")
    public Equipment updateEquipment(@PathVariable("id") Long id, @RequestBody Equipment patch) {
        return equipmentService.updateEquipment(id, patch);
    }

    @DeleteMapping("/equipment/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable("id") Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }

    // --- Reservation CRUD ---

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

    @GetMapping("/search")
    public ResponseEntity<List<ReservationLookupDTO>> searchReservation(
            @RequestParam("phone") String phoneNumber) {

        List<ReservationLookupDTO> result = reservationService.searchReservation(phoneNumber);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reservations")
    public List<ReservationLookupDTO> getReservationByDate(@RequestParam(required = false) LocalDate date) {
        return reservationService.getReservationsByDate(date);
    }

}
