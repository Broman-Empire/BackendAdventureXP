package org.example.adventurexp.service;

import jakarta.transaction.Transactional;
import org.example.adventurexp.dto.*;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Reservation;
import org.example.adventurexp.model.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public interface IReservationService{

    ReservationResponse getReservationById(Long id);

    /**
     * Summerer antal deltagere som allerede er booket for en given aktivitet i et specifikt timeslot.
     */
    int reservedCount(TimeSlot slot, Activity activity);

    @Transactional
    void deleteReservation(Long reservationId);

    @Transactional
    ReservationResponse createReservation(CreateReservationDTO request);

    @Transactional
    ReservationResponse updateReservation(UpdateReservationRequest req, Long id);

    List<BookingScheduleDTO> getDaySchedule(LocalDate date);
  
    List<ReservationLookupDTO> searchReservation(String string);

    List<ReservationLookupDTO> getReservationsByDate(LocalDate date);
}
