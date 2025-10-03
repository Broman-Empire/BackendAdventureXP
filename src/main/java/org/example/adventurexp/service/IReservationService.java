package org.example.adventurexp.service;

import jakarta.transaction.Transactional;
import org.example.adventurexp.dto.CreateReservationDTO;
import org.example.adventurexp.dto.ReservationResponse;
import org.example.adventurexp.dto.UpdateReservationRequest;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;

public interface IReservationService{


    /**
     * Summerer antal deltagere som allerede er booket for en given aktivitet i et specifikt timeslot.
     */
    int reservedCount(TimeSlot slot, Activity activity);

    @Transactional
    void cancelReservation(Long reservationId);

    @Transactional
    ReservationResponse createReservation(CreateReservationDTO request);

    @Transactional
    void updateReservation(UpdateReservationRequest req);
}
