package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.example.adventurexp.service.SlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
public class SlotServiceTest {
    @Autowired
    private IActivityRepository activityRepository;

    @Autowired
    private ITimeSlotRepository timeSlotRepository;

    private SlotService slotService;

    @BeforeEach
    void setUp() {
        slotService = new SlotService(timeSlotRepository, activityRepository);
    }

    @Test
    void generateSlots_createsExpectedSlots() {
        // Arrange: opret en aktivitet
        Activity activity = new Activity();
        activity.setName("Paintball");
        activity.setDurationMinutes(60);
        activity.setMaxParticipants(10);
        activity.setParallelCourts(2);
        activityRepository.save(activity);

        LocalDate fromDate = LocalDate.of(2025, 10, 1);
        LocalDate toDate   = LocalDate.of(2025, 10, 1); // kun én dag
        LocalTime openTime  = LocalTime.of(10, 0);
        LocalTime closeTime = LocalTime.of(14, 0);

        // Act: generér slots
        slotService.generateSlots(activity.getId(), fromDate, toDate, openTime, closeTime);

        // Assert: hent slots fra repo og check
        List<TimeSlot> slots = timeSlotRepository.findByActivity(activity);

        // 10-14 = 4 slots á 60 min × 2 baner = 8 slots i alt
        assertThat(slots).hasSize(8);

        // Check at første slot starter kl. 10:00
        assertThat(slots.get(0).getStartsAt().toLocalTime()).isEqualTo(LocalTime.of(10, 0));
        // Check at sidste slot slutter kl. 14:00
        assertThat(slots.get(slots.size()-1).getEndsAt().toLocalTime()).isEqualTo(LocalTime.of(14, 0));
    }

}
