package org.example.adventurexp.controller;

import org.example.adventurexp.model.Reservation;
import org.example.adventurexp.service.IReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final IReservationService reservationService;

    public AdminController (IReservationService reservationService) {
        this.reservationService = reservationService;
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

}
