package org.example.adventurexp.service;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ISlotService {

    // Opretter tidsrum (slots) for en given aktivitet ud fra:
    // aktivitetens varighed, antal baner samt antal deltagere for hvert slot inden for åbningstiden
    void generateSlots(Long activityId, LocalDate fromDate, LocalDate toDate, LocalTime openTime, LocalTime closeTime);

    void generateSlots(Long activityId, LocalDate fromDate, LocalDate toDate);

}


