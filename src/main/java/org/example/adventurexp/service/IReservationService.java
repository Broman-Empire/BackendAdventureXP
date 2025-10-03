package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;

public interface IReservationService{


    /**
     * Summerer antal deltagere som allerede er booket for en given aktivitet i et specifikt timeslot.
     */
    int reservedCount(TimeSlot slot, Activity activity);
}
