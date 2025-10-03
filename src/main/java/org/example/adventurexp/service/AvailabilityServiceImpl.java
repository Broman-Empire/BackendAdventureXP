package org.example.adventurexp.service;

import org.example.adventurexp.dto.AvailabilityDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityServiceImpl implements IAvailabilityService {

    private final ITimeSlotRepository timeSlotRepository;
    private final IEquipmentService equipmentService;
    private final IReservationService reservationService;

    public AvailabilityServiceImpl(ITimeSlotRepository timeSlotRepository, IEquipmentService equipmentService, IReservationService reservationService) {
        this.timeSlotRepository = timeSlotRepository;
        this.equipmentService = equipmentService;
        this.reservationService = reservationService;
    }

    // Calculates remaining slots for an activity in a given timeslot


    public AvailabilityDTO[] getDailyAvailability(long activityId, LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date must be provided");
        // for nu giver den tomt array tilbage
        return new AvailabilityDTO[0];
    }

    public int computeRemaining(TimeSlot slot, Activity activity) {
        if (slot == null || activity == null) return 0; // Returnér 0 hvis slot eller aktivitet mangler

        int usableSets = equipmentService.usableSets(activity); // Hent antal brugbare udstyrssæt
        int reservedCount = reservationService.reservedCount(slot, activity); // Hent antal reserverede pladser
        int slotCapacity = slot.getCapacity(); // Hent kapacitet for tidsrummet
        int maxPossible = Math.min(slotCapacity, usableSets); // Find det maksimale antal mulige deltagere
        int remaining = maxPossible - reservedCount; // Beregn ledige pladser

        return Math.max(remaining, 0); // Returnér aldrig negativt antal
    }


    public int activeEquipmentSets(long activityId) {
        List<Equipment> equipmentList = equipmentService.getEquipmentByActivityId(activityId);
        if (equipmentList == null || equipmentList.isEmpty()) {
            return 0;
        }
        return equipmentList.stream()
                .mapToInt(Equipment::getUsableSets)
                .sum();
    }

}
    //Chattens forslag til løsning:

    // TODO: Slå slots op for datoen via TimeSlotRepository (startsAt mellem startOfDay og endOfDay)
    // TODO: (Valgfrit) Slå Activity og Equipment op for at beregne effektiv kapacitet (min(slot.capacity, activity.maxParticipants, equipment.usableSets))
    // TODO: Map hvert slot til AvailabilityDTO (start, end, capacity, remaining, soldOut)
    // TODO: Filtrér slots hvor remaining <= 0 (hide optagede)
    // TODO: Returnér som AvailabilityDTO[]
