package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Booking;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock IReservationRepository reservationRepository;
    @Mock IBookingRepository bookingRepository;
    @Mock IActivityRepository activityRepository;
    @Mock ITimeSlotRepository timeSlotRepository;

    private ReservationServiceImpl sut;

    @BeforeEach
    void setUp() {
        sut = new ReservationServiceImpl(reservationRepository, bookingRepository, activityRepository, timeSlotRepository);
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
}
