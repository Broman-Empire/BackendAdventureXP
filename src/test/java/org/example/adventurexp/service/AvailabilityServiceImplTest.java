package org.example.adventurexp.service;

import org.example.adventurexp.dto.AvailabilityDTO;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    ITimeSlotRepository timeSlotRepository;
    @Mock
    IEquipmentService equipmentService;
    @Mock
    IReservationService reservationService;
    @Mock
    ISlotService slotService;
    @Mock
    IActivityRepository activityRepository;

    @InjectMocks
    AvailabilityServiceImpl sut;

    @Test
    void activeEquipmentSets_sumsUsableSets() {
        long activityId = 42L;
        var e1 = new Equipment();
        e1.setUsableSets(3);
        var e2 = new Equipment();
        e2.setUsableSets(5);
        when(equipmentService.getEquipmentByActivityId(activityId))
                .thenReturn(List.of(e1, e2));

        assertEquals(8, sut.activeEquipmentSets(activityId));
    }

    @Test
    void activeEquipmentSets_noEquipment_returnsZero() {
        when(equipmentService.getEquipmentByActivityId(anyLong()))
                .thenReturn(Collections.emptyList());
        assertEquals(0, sut.activeEquipmentSets(1L));
    }

    // --- computeRemaining tests ---
    @Test
    void computeRemaining_returnsCorrectValue() {
        Activity activity = new Activity();
        TimeSlot slot = new TimeSlot();
        slot.setCapacity(10);

        when(equipmentService.usableSets(activity)).thenReturn(8); // 8 usable sets
        when(reservationService.reservedCount(slot, activity)).thenReturn(3); // 3 already reserved

        // maxPossible = min(10, 8) = 8; remaining = 8 - 3 = 5
        assertEquals(5, sut.computeRemaining(slot, activity));
    }

    @Test
    void computeRemaining_returnsZeroForNullSlotOrActivity() {
        assertEquals(0, sut.computeRemaining(null, new Activity())); // Null slot
        assertEquals(0, sut.computeRemaining(new TimeSlot(), null)); // Null activity
        assertEquals(0, sut.computeRemaining(null, null)); // Both null
    }

    @Test
    void computeRemaining_neverReturnsNegative() {
        Activity activity = new Activity();
        TimeSlot slot = new TimeSlot();
        slot.setCapacity(5);

        when(equipmentService.usableSets(activity)).thenReturn(5); // 5 usable sets
        when(reservationService.reservedCount(slot, activity)).thenReturn(10); // 10 already reserved

        assertEquals(0, sut.computeRemaining(slot, activity));
    }

    // --- getDailyAvailability tests ---
    @Test
    void getDailyAvailability_returnsAvailableSlots() {
        long activityId = 1L;
        LocalDate from = LocalDate.of(2025, 10, 6);
        LocalDate to = LocalDate.of(2025, 10, 6);
        LocalTime open = LocalTime.of(10, 0);
        LocalTime close = LocalTime.of(12, 0);

        // prepare 2 slots (1 available, 1 full)
        TimeSlot slot1 = new TimeSlot();
        slot1.setCapacity(5);
        slot1.setStartsAt(LocalDateTime.of(2025, 10, 6, 10, 0));
        slot1.setEndsAt(LocalDateTime.of(2025, 10, 6, 11, 0));

        TimeSlot slot2 = new TimeSlot();
        slot2.setCapacity(5);
        slot2.setStartsAt(LocalDateTime.of(2025, 10, 6, 11, 0));
        slot2.setEndsAt(LocalDateTime.of(2025, 10, 6, 12, 0));

        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setMaxParticipants(5);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity)); // return activity
        when(timeSlotRepository.findByActivityAndStartsAtBetween(activity,
                from.atTime(open), to.atTime(close))).thenReturn(List.of(slot1, slot2));
        // for both slots, usableSets == 5
        when(equipmentService.usableSets(activity)).thenReturn(5);
        // slot1: remaining = min(5,5)-3 = 2
        when(reservationService.reservedCount(slot1, activity)).thenReturn(3);
        // slot2: remaining = min(5,5)-5 = 0 (full)
        when(reservationService.reservedCount(slot2, activity)).thenReturn(5);

        AvailabilityDTO[] result = sut.getDailyAvailability(activityId, from, to, open, close); // execute

        assertEquals(1, result.length); // only 1 available slot
        assertEquals(slot1.getStartsAt(), result[0].getStart());
        assertEquals(slot1.getEndsAt(), result[0].getEnd());
        assertEquals(2, result[0].getRemaining());
        assertFalse(result[0].isSoldOut()); // not sold out
    }

    @Test
    void getDailyAvailability_throwsIfNullDates() {
        assertThrows(IllegalArgumentException.class,
                () -> sut.getDailyAvailability(1L, null, LocalDate.now(), LocalTime.NOON, LocalTime.MIDNIGHT)); // fromDate null
        assertThrows(IllegalArgumentException.class,
                () -> sut.getDailyAvailability(1L, LocalDate.now(), null, LocalTime.NOON, LocalTime.MIDNIGHT)); // toDate null
        assertThrows(IllegalArgumentException.class,
                () -> sut.getDailyAvailability(1L, LocalDate.now(), LocalDate.now(), null, LocalTime.MIDNIGHT)); // openTime null
        assertThrows(IllegalArgumentException.class,
                () -> sut.getDailyAvailability(1L, LocalDate.now(), LocalDate.now(), LocalTime.NOON, null)); // closeTime null
    }
}


