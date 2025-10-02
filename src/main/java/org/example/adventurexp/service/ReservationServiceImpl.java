package org.example.adventurexp.service;

import org.example.adventurexp.dto.CreateReservationDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IBookingRepository;
import org.example.adventurexp.repository.IReservationRepository;

import org.example.adventurexp.model.Booking;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IBookingRepository bookingRepository;
    private final IActivityRepository activityRepository;
    private final ITimeSlotRepository timeSlotRepository;

    public ReservationServiceImpl(IReservationRepository reservationRepository, IBookingRepository bookingRepository, IActivityRepository activityRepository, ITimeSlotRepository timeSlotRepository) {

        this.reservationRepository = reservationRepository;
        this.bookingRepository = bookingRepository;
        this.activityRepository = activityRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    public void ensureNoOverlaps(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return;
        }

        // Slå alle timeslots op for bookings
        List<TimeSlot> timeSlots = bookings.stream()
                .map(b -> timeSlotRepository.findById(b.getTimeSlot().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid timeslot for bookings " + b.getId())))
                .sorted(Comparator.comparing(TimeSlot::getStartsAt))
                .toList();

//        Her står det samme som de 5 ovenstående linjer.
//        List<TimeSlot> timeSlots2 = new ArrayList<>();
//        for (Booking b :  bookings) {
//            timeSlots2.add(b.getTimeSlot());
//        }
//        timeSlots2.sort(Comparator.comparing(TimeSlot::getStartsAt));

        for (int i = 0; i < timeSlots.size() - 1; i++) {
            TimeSlot current = timeSlots.get(i);
            TimeSlot next = timeSlots.get(i + 1);

            if (current.getEndsAt().isAfter(next.getStartsAt())) {
                throw new IllegalArgumentException(
                        String.format("Overlap detected: slot %d [%s - %s] overlaps with slot %d [%s - %s]",
                                current.getId(),
                                current.getStartsAt(),
                                current.getEndsAt(),
                                next.getId(),
                                next.getStartsAt(),
                                next.getEndsAt()
                        )
                );
            }
        }
    }

//    TODO Vi må lige tage stilling til dette, når vi kommer til det.
//    public void ensureNoOverlaps(List<Booking> bookings) {
//        if(bookings == null || bookings.isEmpty()) return;
//
//        for (Booking booking : bookings) {
//            if(booking == null) continue;
//            LocalDateTime start = booking.getTimeSlot().getStartsAt();
//            LocalDateTime end = booking.getTimeSlot().getEndsAt();
//            if(start == null || end == null) {
//                throw new IllegalArgumentException("ReservationItem has null start or end time");
//            }
//            if(!end.isAfter(start)) {
//                throw new IllegalArgumentException("ReservationItem end time must be after start time");
//            }
//        }
//
//        List<Booking> sorted = bookings.stream()
//                .filter(b -> b != null) // we filter null values out
//                .sorted(Comparator
//                        .comparing(Booking::getTimeSlot) // we sort by start time
//                        .thenComparing(Booking::getEndsAt)) // if start time is same, sort by end time
//                .toList();
//
//        LocalDateTime lastEnd = null;
//        for (Booking booking : sorted) {
//            if (lastEnd != null && booking.getTimeSlot().getStartsAt().isBefore(lastEnd)) { // if current start is before last end
//                throw new IllegalArgumentException("Overlapping reservation bookings detected");
//            }
//            lastEnd = booking.getTimeSlot().getEndsAt(); // update lastEnd to current activity end
//        }
//    }

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