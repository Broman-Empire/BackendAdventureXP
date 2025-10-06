package org.example.adventurexp.service;

import org.example.adventurexp.model.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ISlotService {

    // Opretter tidsrum (slots) for en given aktivitet ud fra:
    // aktivitetens varighed, antal baner samt antal deltagere for hvert slot inden for åbningstiden
    List<TimeSlot> generateSlots(Long activityId, LocalDate fromDate, LocalDate toDate, LocalTime openTime, LocalTime closeTime);

}


