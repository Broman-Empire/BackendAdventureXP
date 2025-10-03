package org.example.adventurexp.service;

import org.example.adventurexp.dto.AvailabilityDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;

import java.time.LocalDate;

public interface IAvailabilityService {

    AvailabilityDTO[] getDailyAvailability(long activityId, LocalDate date);

    int computeRemaining(TimeSlot slot, Activity activity);

    /**
     * Returnerer det samlede antal brugbare udstyrssæt for en given aktivitet.
     * @param activityId ID på aktiviteten
     * @return antal brugbare sets
     */
    int activeEquipmentSets(long activityId);
}