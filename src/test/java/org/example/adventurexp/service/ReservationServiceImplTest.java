package org.example.adventurexp.service;

import org.example.adventurexp.dto.CreateReservationDTO;
import org.example.adventurexp.dto.ReservationResponse;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Booking;
import org.example.adventurexp.model.Reservation;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IBookingRepository;
import org.example.adventurexp.repository.IReservationRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock IReservationRepository reservationRepository;
    @Mock IBookingRepository bookingRepository;
    @Mock IActivityRepository activityRepository;
    @Mock ITimeSlotRepository timeSlotRepository;
    @Mock IEquipmentService equipmentService;

    private ReservationServiceImpl sut;

    @BeforeEach
    void setUp() {
        sut = new ReservationServiceImpl(reservationRepository, bookingRepository, activityRepository, timeSlotRepository, equipmentService);
    }

    // helper methods for setup
    private Activity validActivity(Long id, Integer minAge) {
        Activity a = new Activity();
        a.setId(id);
        a.setMinAge(minAge);
        return a;
    }

    private TimeSlot validSlot(Long id, int capacity) {
        TimeSlot ts = new TimeSlot();
        ts.setId(id);
        ts.setCapacity(capacity);
        ts.setStartsAt(LocalDateTime.now().plusDays(1));
        ts.setEndsAt(ts.getStartsAt().plusHours(1));
        return ts;
    }

    private CreateReservationDTO validDTO() {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setCustomerType("private");
        dto.setContactName("Test Testesen");
        dto.setEmail("test@testesen.io");
        dto.setPhone("12345678");
        dto.setGroupMinAge(18);
        dto.setSlotId(101L);
        dto.setActivityId(201L);
        dto.setParticipants(4);
        return dto;
    }

    // --- tests for createReservation ---
    @Test
    void createReservation_success () {
        CreateReservationDTO dto = validDTO();
        Activity activity = validActivity(dto.getActivityId(), 16); // min age 16
        TimeSlot slot = validSlot(dto.getSlotId(), 10); // capacity 10
        Reservation savedReservation = new Reservation();
        savedReservation.setId(1L);

        when(activityRepository.findById(dto.getActivityId())).thenReturn(Optional.of(activity)); // activity found
        when(timeSlotRepository.findById(dto.getSlotId())).thenReturn(Optional.of(slot)); // slot found
        when(equipmentService.usableSets(activity)).thenReturn(8); // 8 sets available
        when(bookingRepository.sumParticipantsByActivityAndSlot(activity.getId(), slot.getId())).thenReturn(0); // no existing bookings
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation); // reservation saved

        ReservationResponse response = sut.createReservation(dto);

        assertNotNull(response);
        assertEquals(1L, response.getId()); // check returned id
        verify(reservationRepository).save(any(Reservation.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createReservation_activityNotFound_throws() {
        CreateReservationDTO dto = validDTO();
        when(activityRepository.findById(dto.getActivityId())).thenReturn(Optional.empty()); // activity not found
        assertThrows(IllegalArgumentException.class, () -> sut.createReservation(dto));
    }

    @Test
    void createReservation_slotNotFound_throws() {
        CreateReservationDTO dto = validDTO();
        Activity activity = validActivity(dto.getActivityId(), 16);
        when(activityRepository.findById(dto.getActivityId())).thenReturn(Optional.of(activity)); // activity found
        when(timeSlotRepository.findById(dto.getSlotId())).thenReturn(Optional.empty()); // slot not found
        assertThrows(IllegalArgumentException.class, () -> sut.createReservation(dto));
    }

    @Test
    void createReservation_notEnoughCapacity_throws() {
        CreateReservationDTO dto = validDTO();
        Activity activity = validActivity(dto.getActivityId(), 16);
        TimeSlot slot = validSlot(dto.getSlotId(), 2); // capacity 2
        when(activityRepository.findById(dto.getActivityId())).thenReturn(Optional.of(activity)); // activity found
        when(timeSlotRepository.findById(dto.getSlotId())).thenReturn(Optional.of(slot)); // slot found
        when(equipmentService.usableSets(activity)).thenReturn(2); // 2 sets available
        when(bookingRepository.sumParticipantsByActivityAndSlot(activity.getId(), slot.getId())).thenReturn(0); // no existing bookings

        assertThrows(IllegalArgumentException.class, () -> sut.createReservation(dto));
    }


    // --- tests for validateReservation ---
    @Test
    void validateReservation_valid_doesNotThrow() {
        CreateReservationDTO dto = validDTO();
        Activity activity = validActivity(dto.getActivityId(), 16);
        TimeSlot slot = validSlot(dto.getSlotId(), 10);

        when(activityRepository.findById(dto.getActivityId())).thenReturn(Optional.of(activity)); // activity found
        when(timeSlotRepository.findById(dto.getSlotId())).thenReturn(Optional.of(slot)); // slot found
        when(equipmentService.usableSets(activity)).thenReturn(8); // 8 sets available
        when(bookingRepository.sumParticipantsByActivityAndSlot(activity.getId(), slot.getId())).thenReturn(0); // no existing bookings

        assertDoesNotThrow(() -> sut.validateReservation(dto));
    }

    @Test
    void validateReservation_tooManyParticipants_throws() {
        CreateReservationDTO dto = validDTO();
        dto.setParticipants(5);
        Activity activity = validActivity(dto.getActivityId(), 16);
        TimeSlot slot = validSlot(dto.getSlotId(), 10);

        when(activityRepository.findById(dto.getActivityId())).thenReturn(Optional.of(activity)); // activity found
        when(timeSlotRepository.findById(dto.getSlotId())).thenReturn(Optional.of(slot)); // slot found
        when(equipmentService.usableSets(activity)).thenReturn(4); // 4 sets available
        when(bookingRepository.sumParticipantsByActivityAndSlot(activity.getId(), slot.getId())).thenReturn(0); // no existing bookings

        assertThrows(IllegalArgumentException.class, () -> sut.validateReservation(dto));
    }

    @Test
    void validateReservation_noCustomerType_throws() {
        CreateReservationDTO dto = validDTO();
        dto.setCustomerType("");
        assertThrows(IllegalArgumentException.class, () -> sut.validateReservation(dto));
    }

    @Test
    void validateReservation_negativeParticipants_throws() {
        CreateReservationDTO dto = validDTO();
        dto.setParticipants(-1);
        assertThrows(IllegalArgumentException.class, () -> sut.validateReservation(dto));
    }

    @Test
    void validateReservation_tooYoungForCompany_throws() {
        CreateReservationDTO dto = validDTO();
        dto.setCustomerType("company");
        dto.setGroupMinAge(14); // too young
        assertThrows(IllegalArgumentException.class, () -> sut.validateReservation(dto));
    }

    @Test
    void validateReservation_groupBelowMinAgeForActivity_throws() {
        CreateReservationDTO dto = validDTO();
        dto.setCustomerType("PRIVATE");
        dto.setGroupMinAge(15); // below activity min age == 16
        Activity activity = validActivity(dto.getActivityId(), 16);
        TimeSlot slot = validSlot(dto.getSlotId(), 10);

        when(activityRepository.findById(dto.getActivityId())).thenReturn(Optional.of(activity)); // activity found
        when(timeSlotRepository.findById(dto.getSlotId())).thenReturn(Optional.of(slot)); // slot found

        assertThrows(IllegalArgumentException.class, () -> sut.validateReservation(dto));
    }

    @Test
    void validateReservation_slotNotFound_throws() {
        CreateReservationDTO dto = validDTO();
        Activity activity = validActivity(dto.getActivityId(), 16);

        when(activityRepository.findById(dto.getActivityId())).thenReturn(Optional.of(activity)); // activity found
        when(timeSlotRepository.findById(dto.getSlotId())).thenReturn(Optional.empty()); // slot not found

        assertThrows(IllegalArgumentException.class, () -> sut.validateReservation(dto));
    }

    // --- tests for cancelReservation ---
    @Test
    void cancelReservation_success() {
        Long reservationId = 55L;
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation)); // reservation found

        sut.cancelReservation(reservationId);

        verify(reservationRepository).delete(reservation);
    }

    @Test
    void cancelReservation_nullId_throws() {
        assertThrows(IllegalArgumentException.class, () -> sut.cancelReservation(null));
    }

    @Test
    void cancelReservation_notFound_throws() {
        Long reservationId = 88L;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty()); // reservation not found
        assertThrows(IllegalArgumentException.class, () -> sut.cancelReservation(reservationId));
    }


    /**
     * Hjælper: Opretter en Booking med et TimeSlot (med id, start og slut)
     * og stubber timeSlotRepository.findById(id) til at returnere det samme slot.
     */
    private Booking bookingWithSlot(long slotId, String startsAt, String endsAt) {
        TimeSlot ts = new TimeSlot();
        ts.setId(slotId);
        ts.setStartsAt(LocalDateTime.parse(startsAt));
        ts.setEndsAt(LocalDateTime.parse(endsAt));

        // repository lookup som metoden bruger
        when(timeSlotRepository.findById(slotId)).thenReturn(Optional.of(ts));

        Booking b = new Booking();
        b.setTimeSlot(ts);
        return b;
    }

    @Test
    void createReservation_succesful() {

    }

    @Test
    void touchingIsAllowed_ok() {
        var a = bookingWithSlot(1L, "2024-01-01T10:00", "2024-01-01T11:00");
        var b = bookingWithSlot(2L, "2024-01-01T11:00", "2024-01-01T12:00"); // "touches" a (tilladt)
        assertDoesNotThrow(() -> sut.ensureNoOverlaps(List.of(a, b)));
    }

    @Test
    void overlappingIntervals_throw() {
        var a = bookingWithSlot(3L, "2024-01-01T10:00", "2024-01-01T11:00");
        var b = bookingWithSlot(4L, "2024-01-01T10:30", "2024-01-01T12:00"); // overlapper a
        assertThrows(IllegalArgumentException.class, () -> sut.ensureNoOverlaps(List.of(a, b)));
    }

    @Test
    void nullOrEmptyList_ok() {
        assertDoesNotThrow(() -> sut.ensureNoOverlaps(null));
        assertDoesNotThrow(() -> sut.ensureNoOverlaps(List.of()));
    }

    @Test
    void reservedCount_sumsParticipants() {
        Activity activity = new Activity();
        activity.setId(10L);
        TimeSlot slot = new TimeSlot();
        slot.setId(20L);

        when(bookingRepository.sumParticipantsByActivityAndSlot(10L, 20L)).thenReturn(7);

        assertEquals(7, sut.reservedCount(slot, activity));
    }

    @Test
    void reservedCount_nullArgs_returnsZero() {
        assertEquals(0, sut.reservedCount(null, null));

        Activity activity = new Activity(); activity.setId(1L);
        assertEquals(0, sut.reservedCount(null, activity));

        TimeSlot slot = new TimeSlot(); slot.setId(1L);
        assertEquals(0, sut.reservedCount(slot, null));
    }

    @Test
    void getDaySchedule_returnsEmpty_whenDateIsNull() {
        var result = sut.getDaySchedule(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(timeSlotRepository);
    }

    @Test
    void getDaySchedule_returnsReservationsForDate() {
        LocalDate date = LocalDate.of(2024, 6, 10);
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        var reservation1 = new org.example.adventurexp.model.Reservation();
        reservation1.setId(1L);
        var booking1 = new Booking();
        booking1.setReservation(reservation1);

        var reservation2 = new org.example.adventurexp.model.Reservation();
        reservation2.setId(2L);
        var booking2 = new Booking();
        booking2.setReservation(reservation2);


        when(bookingRepository.findByTimeSlot_StartsAtBetween(from, to)).thenReturn(List.of(booking1, booking2));

        var result = sut.getDaySchedule(date);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(1L)));
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(2L)));
    }

}
