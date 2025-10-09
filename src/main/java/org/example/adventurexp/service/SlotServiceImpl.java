package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SlotServiceImpl implements ISlotService {

    private final ITimeSlotRepository timeSlotRepository;
    private final IActivityRepository activityRepository;

    public SlotServiceImpl(ITimeSlotRepository timeSlotRepository, IActivityRepository activityRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.activityRepository = activityRepository;
    }

    // ---- Opret standard tidsrum (30 dage frem, kl. 08–22) ----
    @Override
    public void generateDefaultSlotsForActivity(Long activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new IllegalArgumentException("Cannot generate slots — activity " + activityId + " no longer exists");
        }

        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = fromDate.plusDays(30);
        LocalTime openTime = LocalTime.of(8, 0);
        LocalTime closeTime = LocalTime.of(22, 0);

        generateSlots(activityId, fromDate, toDate, openTime, closeTime);
    }

    // ---- Genererer tidsrum ud fra aktivitetens varighed og baner ----
    @Override
    public List<TimeSlot> generateSlots(Long activityId, LocalDate fromDate, LocalDate toDate,
                                        LocalTime openTime, LocalTime closeTime) {

        List<TimeSlot> createdSlots = new ArrayList<>();

        Optional<Activity> optActivity = activityRepository.findById(activityId);
        if (optActivity.isEmpty()) {
            throw new IllegalArgumentException("Activity not found with id: " + activityId);
        }

        Activity activity = optActivity.get();
        if (activity.getId() == null) {
            throw new IllegalArgumentException("Cannot generate slots — activity is scheduled for deletion");
        }

        int durationMinutes = activity.getDurationMinutes();
        int maxParticipants = activity.getMaxParticipants();
        int parallelCourts = activity.getParallelCourts();

        LocalDate currentDate = fromDate;
        while (!currentDate.isAfter(toDate)) {
            LocalDateTime slotStart = currentDate.atTime(openTime);
            LocalDateTime dayEnd = currentDate.atTime(closeTime);

            while (slotStart.plusMinutes(durationMinutes).isBefore(dayEnd)
                    || slotStart.plusMinutes(durationMinutes).equals(dayEnd)) {

                LocalDateTime slotEnd = slotStart.plusMinutes(durationMinutes);

                if (!activityRepository.existsById(activityId)) {
                    System.out.println("Activity " + activityId + " was deleted during slot generation — stopping.");
                    return createdSlots;
                }

                for (int court = 1; court <= parallelCourts; court++) {
                    createIfNotExists(activity, slotStart, slotEnd, maxParticipants, court);
                }

                slotStart = slotEnd;

                if (durationMinutes <= 0) {
                    System.out.println("Duration is 0 for activity " + activityId + ", aborting slot generation.");
                    break;
                }
            }

            currentDate = currentDate.plusDays(1);
        }

        System.out.printf("Finished generating slots for activity %d (%d min, %d courts)%n",
                activityId, durationMinutes, parallelCourts);

        return createdSlots;
    }

    // ---- Regenerer fremtidige slots ----
    public void regenerateFutureSlots(Long activityId, LocalDate fromDate) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        List<TimeSlot> slotsToDelete = timeSlotRepository.findByActivityAndStartsAtAfter(activity, fromDateTime);
        timeSlotRepository.deleteAll(slotsToDelete);

        LocalDate toDate = fromDate.plusDays(30);
        LocalTime openTime = LocalTime.of(8, 0);
        LocalTime closeTime = LocalTime.of(22, 0);

        generateSlots(activityId, fromDate, toDate, openTime, closeTime);
    }

    // ---- Hjælpemetode ----
    private void createIfNotExists(Activity activity, LocalDateTime start, LocalDateTime end, int capacity, int court) {
        Optional<TimeSlot> existingTimeSlot =
                timeSlotRepository.findByActivityAndStartsAtAndCourt(activity, start, court);

        if (existingTimeSlot.isEmpty()) {
            TimeSlot timeslot = new TimeSlot();
            timeslot.setActivity(activity);
            timeslot.setStartsAt(start);
            timeslot.setEndsAt(end);
            timeslot.setCapacity(capacity);
            timeslot.setCourt(court);

            timeSlotRepository.save(timeslot);
            System.out.printf("Created TimeSlot | Activity: %d | %s - %s | Court: %d%n",
                    activity.getId(), start, end, court);
        }
    }
}