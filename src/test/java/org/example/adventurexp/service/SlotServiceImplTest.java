package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SlotServiceImplTest {

    private IActivityRepository activityRepository;
    private ITimeSlotRepository timeSlotRepository;
    private SlotServiceImpl slotServiceImpl;

    @BeforeEach
    void setUp() {
        activityRepository = mock(IActivityRepository.class);
        timeSlotRepository = mock(ITimeSlotRepository.class);
        slotServiceImpl = new SlotServiceImpl(timeSlotRepository, activityRepository);
    }

    @Test
    void generateSlots_createsExpectedNumberOfSlots() {
        // Arrange
        Activity activity = new Activity();
        activity.setId(1L);
        activity.setDurationMinutes(60);
        activity.setMaxParticipants(10);
        activity.setParallelCourts(2);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));

        LocalDate fromDate = LocalDate.of(2025, 10, 1);
        LocalDate toDate   = LocalDate.of(2025, 10, 1); // én dag
        LocalTime openTime  = LocalTime.of(10, 0);
        LocalTime closeTime = LocalTime.of(14, 0);

        // Act
        slotServiceImpl.generateSlots(1L, fromDate, toDate, openTime, closeTime);

        // Assert: fang alle gemte slots
        ArgumentCaptor<TimeSlot> slotCaptor = ArgumentCaptor.forClass(TimeSlot.class);
        verify(timeSlotRepository, times(8)).save(slotCaptor.capture());

        assertThat(slotCaptor.getAllValues()).hasSize(8);

        // Første slot starter 10:00
        assertThat(slotCaptor.getAllValues().get(0).getStartsAt().toLocalTime())
                .isEqualTo(LocalTime.of(10, 0));

        // Sidste slot slutter 14:00
        assertThat(slotCaptor.getAllValues().get(7).getEndsAt().toLocalTime())
                .isEqualTo(LocalTime.of(14, 0));
    }
}
