package org.example.adventurexp.service;

import org.example.adventurexp.dto.AvailabilityDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;

public interface IAvailabilityService {

    AvailabilityDTO[] getDailyAvailability(long activityId, LocalDate fromDate, LocalDate toDate, LocalTime openTime, LocalTime closeTime);

    int computeRemaining(TimeSlot slot, Activity activity);

    /**
     * Returnerer det samlede antal brugbare udstyrssæt for en given aktivitet.
     * @param activityId ID på aktiviteten
     * @return antal brugbare sets
     */
    int activeEquipmentSets(long activityId);
}