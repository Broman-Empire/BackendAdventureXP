package org.example.adventurexp.service;

import org.example.adventurexp.dto.AvailabilityDTO;
import org.springframework.stereotype.Service;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.TimeSlotRepository;

import java.time.LocalDate;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final TimeSlotRepository timeSlotRepository;

    public AvailabilityServiceImpl(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public AvailabilityDTO[] getDailyAvailability(long activityId, LocalDate date) {
        // returnerer et tomt array for nu
        return new AvailabilityDTO[0];
    }

    //Chattens forslag til løsning:

    // TODO: Slå slots op for datoen via TimeSlotRepository (startsAt mellem startOfDay og endOfDay)
    // TODO: (Valgfrit) Slå Activity og Equipment op for at beregne effektiv kapacitet (min(slot.capacity, activity.maxParticipants, equipment.usableSets))
    // TODO: Map hvert slot til AvailabilityDTO (start, end, capacity, remaining, soldOut)
    // TODO: Filtrér slots hvor remaining <= 0 (hide optagede)
    // TODO: Returnér som AvailabilityDTO[]

}