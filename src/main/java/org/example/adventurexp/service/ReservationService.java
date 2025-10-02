package org.example.adventurexp.service;

import org.example.adventurexp.dto.CreateReservationDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IReservationItemRepository;
import org.example.adventurexp.repository.IReservationRepository;

import org.example.adventurexp.model.Booking;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationService {

    private final IReservationRepository reservationRepository;
    private final IReservationItemRepository reservationItemRepository;
    private final IActivityRepository activityRepository;

    public ReservationService(ReservationRepository reservationRepository, ReservationItemRepository reservationItemRepository, ActivityRepository activityRepository) {

        this.reservationRepository = reservationRepository;
        this.reservationItemRepository = reservationItemRepository;
        this.activityRepository = activityRepository;
    }

    public void ensureNoOverlaps(List<Booking> items) {
        if(items == null || items.isEmpty()) return;

        for (Booking item : items) {
            if(item == null) continue;
            LocalDateTime start = item.getStartsAt();
            LocalDateTime end = item.getEndsAt();
            if(start == null || end == null) {
                throw new IllegalArgumentException("ReservationItem has null start or end time");
            }
            if(!end.isAfter(start)) {
                throw new IllegalArgumentException("ReservationItem end time must be after start time");
            }
        }

        List<Booking> sorted = items.stream()
                .filter(i -> i != null) // we filter null values out
                .sorted(Comparator
                        .comparing(Booking::getStartsAt) // we sort by start time
                        .thenComparing(Booking::getEndsAt)) // if start time is same, sort by end time
                .toList();

        LocalDateTime lastEnd = null;
        for (Booking item : sorted) {
            if (lastEnd != null && item.getStartsAt().isBefore(lastEnd)) { // if current start is before last end
                throw new IllegalArgumentException("Overlapping reservation items detected");
            }
            lastEnd = item.getEndsAt(); // update lastEnd to current activity end
        }
    }

    public void validateReservation(Object request) {

/// NOTE: Slot capacity and usable-sets checks are TODO until Slot/Equipment backend is implemented.

        if (!(request instanceof CreateReservationDTO dto)) { // check that request is a CreateReservationDTO
            throw new IllegalArgumentException("Expected CreateReservationDTO");
        }
        if (dto.getCustomerType() == null || dto.getCustomerType().isBlank())
            throw new IllegalArgumentException("Customer type is required");
        if (dto.getParticipants() <= 0)
            throw new IllegalArgumentException("Participants must be > 0");

        // Age rules
        int groupMinAge = dto.getGroupMinAge();
        String type = dto.getCustomerType().trim().toUpperCase();
        if ("COMPANY".equals(type)) {
            if (groupMinAge < 16) {
                throw new IllegalArgumentException("Company bookings require group min age ≥ 16");
            }
        } else if ("PRIVATE".equals(type)) {
            Activity activity = activityRepository.findById(dto.getActivityId())
                    .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + dto.getActivityId()));
            Integer minAge = activity.getMinAge(); // Minigolf can be null = no limit
            if (minAge != null && groupMinAge < minAge) {
                throw new IllegalArgumentException("Group min age below activity minimum (" + minAge + "+), got " + groupMinAge);
            }
        } else {
            throw new IllegalArgumentException("Unknown customer type: " + dto.getCustomerType());
        }

        // TODO: MANGLER Slot model og repository til at fuldføre nedenstående
//        // Slot and capacity checks
//        if (dto.getSlotId() == null) {
//            throw new IllegalArgumentException("slotId is required");
//        }
//        Slot slot = slotRepository.findById(dto.getSlotId())
//                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + dto.getSlotId()));
//        Integer remaining = slot.getRemaining();
//        if (remaining == null || remaining < dto.getParticipants()) {
//            throw new IllegalArgumentException("Not enough remaining capacity in selected slot");
//        }

        // TODO: MANGLER equipment logik til at fuldføre nedenstående
//        // Equipment availability check
//        Integer usableSets = dto.getUsableSets();
//        if (usableSets != null && dto.getParticipants() > usableSets) {
//            throw new IllegalArgumentException("Participants exceed usable equipment available");
//        }
    }
}
