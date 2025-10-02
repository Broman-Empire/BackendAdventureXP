package org.example.adventurexp.service;

import org.example.adventurexp.model.Booking;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IBookingRepository;
import org.example.adventurexp.repository.IReservationRepository;


import org.example.adventurexp.repository.ITimeSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

    @Mock
    IReservationRepository reservationRepository;
    @Mock
    IBookingRepository reservationItemRepository;
    @Mock
    IActivityRepository activityRepository;
    @Mock
    ITimeSlotRepository timeSlotRepository;


    private ReservationServiceImpl service() {
        return new ReservationServiceImpl(reservationRepository, reservationItemRepository, activityRepository, timeSlotRepository);
    }

    private Booking item(String startsAt, String endsAt) {
        Booking item = new Booking();
        item.getTimeSlot().setStartsAt(LocalDateTime.parse(startsAt));
        item.getTimeSlot().setEndsAt(LocalDateTime.parse(endsAt));
        return item;
    }

    @Test
    public void touchingIsAllowed_ok() {
        var a = item("2024-01-01T10:00", "2024-01-01T11:00");
        var b = item("2024-01-01T11:00", "2024-01-01T12:00"); // touches a
        assertDoesNotThrow(() -> service().ensureNoOverlaps(List.of(a, b))); // passes if no exception is thrown
    }

    @Test
    public void overlappingIntervals_throw() {
        var a = item("2024-01-01T10:00", "2024-01-01T11:00");
        var b = item("2024-01-01T10:30", "2024-01-01T12:00"); // overlaps a
        assertThrows(IllegalArgumentException.class, () -> service().ensureNoOverlaps(List.of(a, b))); // passes if exception is thrown
    }

    @Test
    public void invalidInterval_endNotAfterStart_throw() {
        var a = item("2024-01-01T10:00", "2024-01-01T09:00"); // end == start
        assertThrows(IllegalArgumentException.class, () -> service().ensureNoOverlaps(List.of(a))); // passes if exception is thrown
    }

    @Test
    public void nullOrEmptyList_ok() {
        assertDoesNotThrow(() -> service().ensureNoOverlaps(null));
        assertDoesNotThrow(() -> service().ensureNoOverlaps(List.of())); // passes if no exception is thrown
    }


    // TODO: write tests for validateReservation when Slot/Equipment backend is implemented

}
