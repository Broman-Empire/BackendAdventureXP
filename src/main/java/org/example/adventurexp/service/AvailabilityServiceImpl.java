package org.example.adventurexp.service;

import org.example.adventurexp.dto.AvailabilityDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityServiceImpl {

    private final ITimeSlotRepository iTimeSlotRepository;
    private final IEquipmentService iEquipmentService;
    private final IReservationService iReservationService;
    private final ISlotService iSlotService;
    private final IActivityRepository iActivityRepository;

    public AvailabilityServiceImpl(ITimeSlotRepository iTimeSlotRepository, IEquipmentService iEquipmentService,
                                   IReservationService iReservationService, ISlotService iSlotService, IActivityRepository iActivityRepository) {
        this.iTimeSlotRepository = iTimeSlotRepository;
        this.iEquipmentService = iEquipmentService;
        this.iReservationService = iReservationService;
        this.iSlotService = iSlotService;
        this.iActivityRepository = iActivityRepository;
    }

    // Calculates remaining slots for an activity in a given timeslot
    @Override
    public AvailabilityDTO[] getDailyAvailability(long activityId, LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date must be provided"); // Tjek for null dato

        List<TimeSlot> slots = iSlotService.generateSlots(activityId, date);
        List<AvailabilityDTO> availableSlots = new ArrayList<>();

        for (TimeSlot slot : slots) {
            // findById returnerer Optional<Activity>, så brug .orElse(null)
            Activity activity = iActivityRepository.findById(activityId).orElse(null);
            int remaining = computeRemaining(slot, activity);
            if (remaining > 0) {
                AvailabilityDTO dto = new AvailabilityDTO(
                    slot.getStartsAt(), // Starttidspunkt
                    slot.getEndsAt(),   // Sluttidspunkt
                    slot.getCapacity(), // Kapacitet
                    remaining,          // Ledige pladser
                    remaining == 0      // Udsolgt
                );
                availableSlots.add(dto);
            }
        }
        // Returnér som array
        return availableSlots.toArray(new AvailabilityDTO[0]);
    }

    /**
     * Beregner antal ledige pladser for en aktivitet i et givent slot.
     * TODO: Eftertjek metoden når TimeSlot-klassen er færdig og integreret.
     */
    @Override
    public int computeRemaining(TimeSlot slot, Activity activity) {
        if (slot == null || activity == null) return 0; // Returnér 0 hvis slot eller aktivitet mangler

        int usableSets = iEquipmentService.usableSets(activity); // Hent antal brugbare udstyrssæt
        int reservedCount = iReservationService.reservedCount(slot, activity); // Hent antal reserverede pladser
        int slotCapacity = slot.getCapacity(); // Hent kapacitet for tidsrummet
        int maxPossible = Math.min(slotCapacity, usableSets); // Find det maksimale antal mulige deltagere
        int remaining = maxPossible - reservedCount; // Beregn ledige pladser

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
