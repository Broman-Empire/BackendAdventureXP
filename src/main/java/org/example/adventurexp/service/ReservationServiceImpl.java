package org.example.adventurexp.service;

import jakarta.transaction.Transactional;
import org.example.adventurexp.dto.CreateReservationDTO;
import org.example.adventurexp.dto.ReservationResponse;
import org.example.adventurexp.dto.UpdateReservationRequest;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Reservation;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IBookingRepository;
import org.example.adventurexp.repository.IReservationRepository;

import org.example.adventurexp.model.Booking;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IBookingRepository bookingRepository;
    private final IActivityRepository activityRepository;
    private final ITimeSlotRepository timeSlotRepository;
    private final IEquipmentService equipmentService;

    public ReservationServiceImpl(IReservationRepository reservationRepository,
                                  IBookingRepository bookingRepository,
                                  IActivityRepository activityRepository,
                                  ITimeSlotRepository timeSlotRepository,
                                  IEquipmentService equipmentService) {
        this.reservationRepository = reservationRepository;
        this.bookingRepository = bookingRepository;
        this.activityRepository = activityRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.equipmentService = equipmentService;
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

        // Slot and capacity checks
        if (dto.getSlotId() == null) {
            throw new IllegalArgumentException("slotId is required");
        }
        TimeSlot slot = timeSlotRepository.findById(dto.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + dto.getSlotId()));

        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + dto.getActivityId()));

        // Calculate remaining capacity
        int usableSets = equipmentService.usableSets(activity);
        int reservedCount = reservedCount(slot, activity);
        int maxPossible = Math.min(slot.getCapacity(), usableSets);
        int remaining = maxPossible - reservedCount;

        if (remaining < dto.getParticipants()) {
            throw new IllegalArgumentException("Not enough remaining capacity in selected slot. Available: " + remaining + ", requested: " + dto.getParticipants());
        }

        // TODO: MANGLER equipment logik til at fuldfÃ¸re nedenstÃ¥ende
//        // Equipment availability check
//        Integer usableSets = dto.getUsableSets();
//        if (usableSets != null && dto.getParticipants() > usableSets) {
//            throw new IllegalArgumentException("Participants exceed usable equipment available");
//        }
    }

    @Override
    public int reservedCount(TimeSlot slot, Activity activity) {
        if (slot == null || slot.getId() == null || activity == null || activity.getId() == null) {
            return 0;
        }
        return bookingRepository.sumParticipantsByActivityAndSlot(activity.getId(), slot.getId());
    }

    @Transactional
    @Override
    public void cancelReservation(Long reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        // Delete reservation - cascade will handle deleting associated bookings
        // No need to manually adjust reservedCount since we calculate it dynamically via reservedCount() method
        reservationRepository.delete(reservation);
    }


    // Note: Reserved count tracking removed - we calculate it dynamically via reservedCount() method instead


    @Transactional
    @Override
    public ReservationResponse createReservation(CreateReservationDTO request) {
        // Step 1: Validate the reservation
        validateReservation(request);

        // Step 2: Fetch Activity and TimeSlot
        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + request.getActivityId()));
        TimeSlot slot = timeSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("TimeSlot not found: " + request.getSlotId()));

        // Step 3: Create and save Reservation
        Reservation reservation = new Reservation();
        reservation.setCustomerType(request.getCustomerType());
        reservation.setContactName(request.getContactName());
        reservation.setEmail(request.getEmail());
        reservation.setPhone(request.getPhone());
        reservation.setCreatedAt(LocalDateTime.now());
        reservation = reservationRepository.save(reservation);

        // Step 4: Create and save Booking
        Booking booking = new Booking();
        booking.setReservation(reservation);
        booking.setActivity(activity);
        booking.setTimeSlot(slot);
        booking.setParticipants(request.getParticipants());
        bookingRepository.save(booking);

        // Step 5: Apply capacity changes (if using reserved count tracking)
        // addToSlotReservedCount(slot, request.getParticipants());

        // Step 6: Return ReservationResponse
        return new ReservationResponse(
                reservation.getId(),
                activity.getId(),
                (long) booking.getParticipants(),
                (long) booking.getParticipants(),
                slot.getStartsAt()
        );
    }



    @Transactional
    @Override
    public void updateReservation(UpdateReservationRequest req) {
        if (req == null || req.getReservationId() == null) {
            throw new IllegalArgumentException("Update request and reservation ID are required");
        }

        // Fetch existing reservation
        Reservation reservation = reservationRepository.findById(req.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + req.getReservationId()));

        // Update contact information if provided
        if (req.getContactName() != null && !req.getContactName().isBlank()) {
            reservation.setContactName(req.getContactName());
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            reservation.setEmail(req.getEmail());
        }
        if (req.getPhone() != null && !req.getPhone().isBlank()) {
            reservation.setPhone(req.getPhone());
        }

        // Find the booking associated with this reservation (assuming one booking per reservation for now)
        List<Booking> bookings = bookingRepository.findByReservationId(req.getReservationId());
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("No bookings found for reservation: " + req.getReservationId());
        }

        Booking booking = bookings.get(0); // Get first booking

        // Handle activity or timeslot changes
        boolean activityChanged = req.getActivityId() != null && !req.getActivityId().equals(booking.getActivity().getId());
        boolean participantsChanged = req.getNewParticipants() != null && !req.getNewParticipants().equals(booking.getParticipants());

        if (activityChanged || req.getNewStart() != null || participantsChanged) {
            // Fetch new activity if changed
            Activity newActivity = booking.getActivity();
            if (activityChanged) {
                newActivity = activityRepository.findById(req.getActivityId())
                        .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + req.getActivityId()));
            }

            // Find new timeslot if start time changed
            TimeSlot newSlot = booking.getTimeSlot();
            if (req.getNewStart() != null) {
                // Note: This assumes you have a way to find slots by start time and activity
                // You may need to add a query method to ITimeSlotRepository
                throw new UnsupportedOperationException("Changing reservation start time requires additional repository method");
                // Example: newSlot = timeSlotRepository.findByActivityIdAndStartsAt(newActivity.getId(), req.getNewStart())
                //              .orElseThrow(() -> new IllegalArgumentException("No slot found for the requested time"));
            }

            // Validate new booking parameters
            int newParticipantCount = req.getNewParticipants() != null ? req.getNewParticipants() : booking.getParticipants();

            // Check capacity for new slot
            int usableSets = equipmentService.usableSets(newActivity);
            int reservedCount = reservedCount(newSlot, newActivity);

            // Subtract current booking's participants if it's the same slot (to avoid counting it twice)
            if (newSlot.getId().equals(booking.getTimeSlot().getId()) && newActivity.getId().equals(booking.getActivity().getId())) {
                reservedCount -= booking.getParticipants();
            }

            int maxPossible = Math.min(newSlot.getCapacity(), usableSets);
            int remaining = maxPossible - reservedCount;

            if (remaining < newParticipantCount) {
                throw new IllegalArgumentException("Not enough capacity in selected slot. Available: " + remaining + ", requested: " + newParticipantCount);
            }

            // Update booking
            booking.setActivity(newActivity);
            booking.setTimeSlot(newSlot);
            booking.setParticipants(newParticipantCount);
            bookingRepository.save(booking);
        }

        // Save updated reservation
        reservationRepository.save(reservation);
    }

//    @Override
//    public void deleteReservation(Long reservationId) {
//    }


}