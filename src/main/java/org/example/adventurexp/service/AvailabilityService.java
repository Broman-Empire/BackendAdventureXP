package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot; // manglende model klasse
import org.example.adventurexp.repository.IReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {

    private final IReservationRepository reservationRepository;
    private final EquipmentService equipmentService;
    private final ReservationService reservationService;

    public AvailabilityService(IReservationRepository reservationRepository, EquipmentService equipmentService, ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.equipmentService = equipmentService;
        this.reservationService = reservationService;
    }

    // Beregner antallet af ledige pladser for en aktivitet i et givent tidsrum (timeSlot)
    // Tager højde for kapacitet, udstyr og eksisterende reservationer
    public int computeRemaining(TimeSlot slot, Activity activity) {
        if (slot == null || activity == null) return 0; // Returnér 0 hvis slot eller aktivitet mangler

        int usableSets = equipmentService.usableSets(activity); // Hent antal brugbare udstyrssæt
        int reservedCount = reservationService.reservedCount(slot, activity); // Hent antal reserverede pladser
        int slotCapacity = slot.getCapacity(); // Hent kapacitet for tidsrummet
        int maxPossible = Math.min(slotCapacity, usableSets); // Find det maksimale antal mulige deltagere
        int remaining = maxPossible - reservedCount; // Beregn ledige pladser

        return Math.max(remaining, 0); // Returnér aldrig negativt antal
    }

}
