package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;


@Service
public class SlotServiceImpl implements ISlotService {


    private final ITimeSlotRepository timeSlotRepository;
    private final IActivityRepository activityRepository;

    public SlotServiceImpl(ITimeSlotRepository timeSlotRepository, IActivityRepository activityRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.activityRepository = activityRepository;
    }

    // Opretter tidsrum (slots) for en given aktivitet (hvis den oprettes/ændres) ud fra:
    // aktivitetens varighed (duration), antal baner (parallelCourts)
    // samt antal deltagere (capacity) for hvert slot inden for åbningstiden
    @Override
    public void generateSlots(Long activityId, LocalDate fromDate, LocalDate toDate, LocalTime openTime, LocalTime closeTime) {

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

                // Opret et slot hver hver "parallelCourt"
                for (int court = 1; court <= parallelCourts; court++) {
                    TimeSlot slot = new TimeSlot();
                    slot.setActivity(activity);
                    slot.setStartsAt(slotStart);
                    slot.setEndsAt(slotEnd);
                    slot.setCourt(court);
                    slot.setCapacity(maxParticipants); // max antal deltagere pr. slot

                    timeSlotRepository.save(slot);
                }
                // Et slot kan starte, når et andet slot slutter
                slotStart = slotEnd;
            }
            // Når vi har genereret slots for én dag, genererer vi slots for den næste dag
            currentDate = currentDate.plusDays(1);

        }

    }
}
