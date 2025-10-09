package org.example.adventurexp.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.adventurexp.dto.*;
import org.example.adventurexp.mapper.ReservationMapper;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Reservation;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IBookingRepository;
import org.example.adventurexp.repository.IReservationRepository;

import org.example.adventurexp.model.Booking;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository iReservationRepository;
    private final IBookingRepository iBookingRepository;
    private final IActivityRepository iActivityRepository;
    private final ITimeSlotRepository iTimeSlotRepository;
    private final IEquipmentService iEquipmentService;

    public ReservationServiceImpl(IReservationRepository iReservationRepository, IBookingRepository iBookingRepository,
                                  IActivityRepository iActivityRepository, ITimeSlotRepository iTimeSlotRepository, IEquipmentService iEquipmentService) {

        this.iReservationRepository = iReservationRepository;
        this.iBookingRepository = iBookingRepository;
        this.iActivityRepository = iActivityRepository;
        this.iTimeSlotRepository = iTimeSlotRepository;
        this.iEquipmentService = iEquipmentService;

    }

    @Override
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = iReservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));

        // handle empty bookings
        if (reservation.getBookings() == null || reservation.getBookings().isEmpty()) {
            return new ReservationResponse(reservation.getId(), null, 0L, null);
        }
        // use first booking to fetch activityId, participants, startsAt
        Booking firstBooking = reservation.getBookings().getFirst();
        Long activityId = firstBooking.getActivity() != null ? firstBooking.getActivity().getId() : null;
        Long participants = (long) firstBooking.getParticipants();
        LocalDateTime startsAt = firstBooking.getTimeSlot() != null ? firstBooking.getTimeSlot().getStartsAt() : null;

        // sum all participants across all bookings
        Long totalParticipants = reservation.getBookings().stream()
                .mapToLong(Booking::getParticipants)
                .sum();

        return new ReservationResponse(
                reservation.getId(),
                activityId,
                participants,
        //        totalParticipants,
                startsAt
        );
    }

    public void ensureNoOverlaps(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return;
        }

        // Slå alle timeslots op for bookings
        List<TimeSlot> timeSlots = bookings.stream()
                .map(b -> iTimeSlotRepository.findById(b.getTimeSlot().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid timeslot for bookings " + b.getId())))
                .sorted(Comparator.comparing(TimeSlot::getStartsAt))
                .toList();

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
            Activity activity = iActivityRepository.findById(dto.getActivityId())
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
        TimeSlot slot = iTimeSlotRepository.findById(dto.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + dto.getSlotId()));

        Activity activity = iActivityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + dto.getActivityId()));

        // Calculate remaining capacity
        int usableSets = iEquipmentService.usableSets(activity);
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
        return iBookingRepository.sumParticipantsByActivityAndSlot(activity.getId(), slot.getId());
    }

    @Transactional
    @Override
    public void cancelReservation(Long reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        Reservation reservation = iReservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        // Delete reservation - cascade will handle deleting associated bookings
        // No need to manually adjust reservedCount since we calculate it dynamically via reservedCount() method
        iReservationRepository.delete(reservation);
    }


    // Note: Reserved count tracking removed - we calculate it dynamically via reservedCount() method instead


    @Transactional
    @Override
    public ReservationResponse createReservation(CreateReservationDTO request) {
        // Step 1: Validate the reservation
        validateReservation(request);

        // Step 2: Fetch Activity and TimeSlot
        Activity activity = iActivityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + request.getActivityId()));
        TimeSlot slot = iTimeSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("TimeSlot not found: " + request.getSlotId()));

        // Step 3: Create and save Reservation
        Reservation reservation = new Reservation();
        reservation.setCustomerType(request.getCustomerType());
        reservation.setContactName(request.getContactName());
        reservation.setEmail(request.getEmail());
        reservation.setPhone(request.getPhone());
        reservation.setCreatedAt(LocalDateTime.now());
        reservation = iReservationRepository.save(reservation);

        // Step 4: Create and save Booking
        Booking booking = new Booking();
        booking.setReservation(reservation);
        booking.setActivity(activity);
        booking.setTimeSlot(slot);
        booking.setParticipants(request.getParticipants());
        iBookingRepository.save(booking);

        // Step 5: Apply capacity changes (if using reserved count tracking)
        // addToSlotReservedCount(slot, request.getParticipants());

        // Step 6: Return ReservationResponse
        return new ReservationResponse(
                reservation.getId(),
                activity.getId(),
                (long) booking.getParticipants(),
                slot.getStartsAt()
        );
    }

    /**
     * Applies capacity changes when creating a new booking.
     * Since we calculate reservedCount dynamically, this method is primarily for validation.
     *
     * @param booking The booking item being created
     */
    private void applyCapacityOnCreate(Booking booking) {
        if (booking == null || booking.getTimeSlot() == null || booking.getActivity() == null) {
            throw new IllegalArgumentException("Booking, timeSlot, and activity are required");
        }

        TimeSlot slot = booking.getTimeSlot();
        Activity activity = booking.getActivity();
        int participants = booking.getParticipants();

        // Calculate current capacity
        int usableSets = iEquipmentService.usableSets(activity);
        int reservedCount = reservedCount(slot, activity);
        int maxPossible = Math.min(slot.getCapacity(), usableSets);
        int remaining = maxPossible - reservedCount;

        // Validate that we have capacity
        if (remaining < participants) {
            throw new IllegalArgumentException(
                    String.format("Insufficient capacity for slot %d. Available: %d, requested: %d",
                            slot.getId(), remaining, participants)
            );
        }

        // No need to manually increment - the booking is saved and will be counted in next reservedCount() call
    }


    @Transactional
    @Override
    public ReservationResponse updateReservation(UpdateReservationRequest req, Long id) {
        if (req == null || id == null) {
            throw new IllegalArgumentException("Update request and reservation ID are required");
        }

        // Fetch existing reservation
        Reservation reservation = iReservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));

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
        List<Booking> bookings = iBookingRepository.findByReservationId(id);
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("No bookings found for reservation: " + id);
        }

        Booking booking = bookings.get(0); // Get first booking

        // Handle activity or timeslot changes
        boolean activityChanged = req.getActivityId() != null && !req.getActivityId().equals(booking.getActivity().getId());
        boolean participantsChanged = req.getNewParticipants() != null && !req.getNewParticipants().equals(booking.getParticipants());

        if (activityChanged || req.getNewStart() != null || participantsChanged) {
            // Fetch new activity if changed
            Activity newActivity = booking.getActivity();
            if (activityChanged) {
                newActivity = iActivityRepository.findById(req.getActivityId())
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
            int usableSets = iEquipmentService.usableSets(newActivity);
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
            iBookingRepository.save(booking);
        }

        // Save updated reservation
        iReservationRepository.save(reservation);
        return ReservationMapper.toReservationResponse(
                reservation,
                booking.getActivity().getId(),
                (long) booking.getParticipants(),
                booking.getTimeSlot()
        );
    }

    // ---- Dagsplan til app.schedule.admin.js ----

    @Override
    public List<BookingScheduleDTO> getDaySchedule(LocalDate date) {
        if (date == null) return List.of();

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        List<Booking> bookings = iBookingRepository.findByTimeSlot_StartsAtBetween(from, to);
        if (bookings == null || bookings.isEmpty()) return List.of();

        return bookings.stream()
                .map(b -> {
                    BookingScheduleDTO dto = new BookingScheduleDTO();
                    dto.setBookingId(b.getId());
                    dto.setParticipants(b.getParticipants());

                    // Sæt standardværdier
                    dto.setActivityName("Ukendt aktivitet");
                    dto.setTotalParticipants(0);

                    // Hvorfor alle de if's? = undgå nullPointerException, hvis bookingens relationer ikke er sat
                    // (Activity, TimeSlot, Reservation) ikke er sat på booking, men vi har nullable = false

                    // --- Activity ---
                    Activity activity = b.getActivity();
                    if (activity != null) {
                        dto.setActivityName(activity.getName());
                    }

                    // --- TimeSlot ---
                    TimeSlot slot = b.getTimeSlot();
                    if (slot != null) {
                        dto.setStartsAt(slot.getStartsAt());
                        dto.setEndsAt(slot.getEndsAt());
                        dto.setTotalParticipants(slot.getCapacity());
                    }

                    // --- Reservation ---
                    Reservation reservation = b.getReservation();
                    if (reservation != null) {
                        dto.setReservationId(reservation.getId());
                        dto.setContactName(reservation.getContactName());
                        dto.setCustomerType(reservation.getCustomerType());
                    }

                    return dto;
                })
                .sorted(Comparator.comparing(BookingScheduleDTO::getStartsAt))
                .toList();
    }

    // Slet en reservation ud fra id
    @Override
    public void deleteReservation(Long reservationId) {
        if (!iReservationRepository.existsById(reservationId)) {
            throw new EntityNotFoundException("Reservation not found");
        }
        iReservationRepository.deleteById(reservationId);
    }

    // Søger efter reservation baseret på telefonnummer
    @Override
    public List<ReservationLookupDTO> searchReservation(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number must be provided");
        }

        var reservations = iReservationRepository.findByPhone(phoneNumber);

        if (reservations.isEmpty()) {
            throw new EntityNotFoundException("No reservation made with this phone number: " + phoneNumber);
        }

        return ReservationMapper.toLookupDTOList(reservations);
    }

    @Override
    public List<ReservationLookupDTO> getReservationsByDate(LocalDate date) {
        List<Reservation> reservations;

        if (date == null) {
            reservations = iReservationRepository.findAll();
        } else {
            reservations = iReservationRepository.findByBookingDate(date);
        }

        return reservations.stream().map(ReservationMapper::toLookupDTO).toList();
    }

}