package org.example.adventurexp.controller;

import org.example.adventurexp.dto.CreateReservationDTO;
import org.example.adventurexp.dto.ReservationResponse;
import org.example.adventurexp.dto.UpdateReservationRequest;
import org.example.adventurexp.service.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final IReservationService reservationService;

    @Autowired
    public ReservationController (IReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody CreateReservationDTO request) {
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.ok(response);
    }


}
