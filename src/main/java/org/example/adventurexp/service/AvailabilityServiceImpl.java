package org.example.adventurexp.service;

import org.example.adventurexp.dto.AvailabilityDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityServiceImpl implements IAvailabilityService {

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
    public AvailabilityDTO[] getDailyAvailability(long activityId, LocalDate fromDate, LocalDate toDate, LocalTime openTime, LocalTime closeTime) {
        if (fromDate == null || toDate == null || openTime == null || closeTime == null)
            throw new IllegalArgumentException("Date must be provided");

        Activity activity = iActivityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));

        LocalDateTime from = fromDate.atTime(openTime); // Start of the day at opening time
        LocalDateTime to = toDate.atTime(closeTime); // End of the day at closing time

        List<TimeSlot> slots = iTimeSlotRepository.findByActivityAndStartsAtBetween(activity, from, to); // get timeslots from DB

        int usableSets = Math.max(iEquipmentService.usableSets(activity), 0);
        if (usableSets == 0 && activity.getMaxParticipants() > 0) { // if no usableSet
            usableSets = activity.getMaxParticipants(); // use activity max participants
        }
        List<AvailabilityDTO> result = new ArrayList<>();

        for (TimeSlot slot : slots) {
            int slotCapacity = slot.getCapacity();
            if (slotCapacity <= 0 && activity.getMaxParticipants() > 0) {  //if no slot capacity
                slotCapacity = activity.getMaxParticipants(); // use activity max participants
            }

            int reserved = iReservationService.reservedCount(slot, activity); // get reserved count
            int max = Math.min(slotCapacity, usableSets); // max possible pax
            int remaining = Math.max(max - reserved, 0); // calculate remaining, never negative

            if (remaining > 0) { // only include slots with remaining > 0
                result.add(new AvailabilityDTO(
                        slot.getStartsAt(),
                        slot.getEndsAt(),
                        slotCapacity,
                        remaining,
                        false
                ));
            }
        }
        return result.toArray(new AvailabilityDTO[0]); // return as array
    }


    @Override
    public int computeRemaining(TimeSlot slot, Activity activity) {
        if (slot == null || activity == null) return 0; // Returnér 0 hvis slot eller aktivitet mangler

        int usableSets = iEquipmentService.usableSets(activity); // Hent antal brugbare udstyrssæt
        int reservedCount = iReservationService.reservedCount(slot, activity); // Hent antal reserverede pladser
        int slotCapacity = slot.getCapacity(); // Hent kapacitet for tidsrummet
        int maxPossible = Math.min(slotCapacity, usableSets); // Find det maksimale antal mulige deltagere
        int remaining = maxPossible - reservedCount; // Beregn ledige pladser

        return Math.max(remaining, 0); // Returnér aldrig negativt antal
    }

    @Override
    public int activeEquipmentSets(long activityId) {
        List<Equipment> equipmentList = iEquipmentService.getEquipmentByActivityId(activityId);
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
