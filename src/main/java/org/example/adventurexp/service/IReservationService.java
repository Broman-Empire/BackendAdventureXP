package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;

public interface IReservationService {

    int reservedCount(TimeSlot timeslot, Activity activity);

}
