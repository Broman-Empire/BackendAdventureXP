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
import java.util.Optional;
import java.util.List;

@Service
public class SlotServiceImpl implements ISlotService {


    private final ITimeSlotRepository timeSlotRepository;
    private final IActivityRepository activityRepository;

    public SlotServiceImpl(ITimeSlotRepository timeSlotRepository, IActivityRepository activityRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.activityRepository = activityRepository;
    }

    // ---- Opretter tidsrum (slots) for en given aktivitet (hvis den oprettes/ændres) ud fra:
    // aktivitetens varighed (duration), antal baner (parallelCourts)
    // samt antal deltagere (capacity) for hvert slot inden for åbningstiden

    @Override
    public void generateDefaultSlotsForActivity(Long activityId) {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = fromDate.plusDays(30);
        LocalTime openTime = LocalTime.of(8, 0);
        LocalTime closeTime = LocalTime.of(22, 0);

        generateSlots(activityId, fromDate, toDate, openTime, closeTime);

    }

    //
    @Override
    public List<TimeSlot> generateSlots(Long activityId, LocalDate fromDate, LocalDate toDate, LocalTime openTime, LocalTime closeTime) {

        List<TimeSlot> createdSlots = new ArrayList<>();

        Optional<Activity> optActivity = activityRepository.findById(activityId);

        if (optActivity.isEmpty()) {
            throw new IllegalArgumentException("Activity not found with id: " + activityId);
        }
        Activity activity = optActivity.get();

        int durationMinutes = activity.getDurationMinutes();
        int maxParticipants = activity.getMaxParticipants();
        int parallelCourts = activity.getParallelCourts();

        // Looper over alle datoer i perioden
        LocalDate currentDate = fromDate;
        while (!currentDate.isAfter(toDate)) {
            LocalDateTime slotStart = currentDate.atTime(openTime);
            LocalDateTime dayEnd = currentDate.atTime(closeTime);

            // Looper så længe, der er plads til et nyt slot
            while (slotStart.plusMinutes(durationMinutes).isBefore(dayEnd)
                    || slotStart.plusMinutes(durationMinutes).equals(dayEnd)) {

                LocalDateTime slotEnd = slotStart.plusMinutes(durationMinutes);

                // Opret et slot hver hver "parallelCourt" = flere baner i samme tidsrum
                for (int court = 1; court <= parallelCourts; court++) {
                    createIfNotExists(activityId, slotStart, slotEnd, maxParticipants, court);
                }
                // Et slot kan starte, når et andet slot slutter
                slotStart = slotEnd;
            }
            // Når vi har genereret slots for én dag, genererer vi slots for den næste dag
            currentDate = currentDate.plusDays(1);

        }
        return createdSlots;
    }

    public void regenerateFutureSlots(Long activityId, LocalDate fromDate) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));

        // Delete existing slots from 'fromDate' onwards
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        List<TimeSlot> slotsToDelete = timeSlotRepository.findByActivityAndStartsAtAfter(activity, fromDateTime);
        timeSlotRepository.deleteAll(slotsToDelete);

        // Regenerate slots (daily from 08 to 22)
        LocalDate toDate = fromDate.plusDays(30); // Regenerate slots for the next 30 days
        LocalTime openTime = LocalTime.of(8, 0); // opens at 08:00
        LocalTime closeTime = LocalTime.of(22, 0); // closes at 22:00

        generateSlots(activityId, fromDate, toDate, openTime, closeTime);
    }



    // ---- Hjælpemetode ----
// Opretter et slot, hvis der ikke allerede findes et slot med samme starttidspunkt og bane (activityId + start + court = unik)
    public void createIfNotExists(Long activityId, LocalDateTime start, LocalDateTime end, int capacity, int court) {

        Optional<Activity> optActivity = activityRepository.findById(activityId);
        if (optActivity.isEmpty()) {
            throw new IllegalArgumentException("Activity not found with id: " + activityId);
        }

        Activity activity = optActivity.get();

        // Tjekker om der findes et slot for samme aktivitet + start + court
        Optional<TimeSlot> existingTimeSlot = timeSlotRepository.findByActivityAndStartsAtAndCourt(activity, start, court);

        if (existingTimeSlot.isEmpty()) {
            TimeSlot timeslot = new TimeSlot();
            timeslot.setActivity(activity);
            timeslot.setStartsAt(start);
            timeslot.setEndsAt(end);
            timeslot.setCapacity(capacity);
            timeslot.setCourt(court);

            timeSlotRepository.save(timeslot);
        }
    }
}