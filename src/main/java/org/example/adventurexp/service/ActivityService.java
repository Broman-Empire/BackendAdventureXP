package org.example.adventurexp.service;

import jakarta.transaction.Transactional;
import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.exception.ActivityHasBookingsException;
import org.example.adventurexp.mapper.ActivityMapper;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IBookingRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ActivityService implements IActivityService {

    //IBookingService bookingService; // TODO Den findes ikke, men skal jo eksistere på et tidspunkt
    IActivityRepository activityRepository;
    ITimeSlotRepository timeSlotRepository;
    IBookingRepository bookingRepository;
    ISlotService slotService;

    public ActivityService(IActivityRepository activityRepository,
                           ITimeSlotRepository timeSlotRepository,
                           IBookingRepository bookingRepository,
                           ISlotService slotService) {
        this.activityRepository = activityRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.bookingRepository = bookingRepository;
        this.slotService = slotService;
    }

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Override
    public List<ActivityDTO> findAll() {

        // Debugger
        long count = activityRepository.count();
        System.out.println("Antal rækker i activity: " + count);

        return activityRepository.findAll()
                .stream()// Laver listen: "pipeline"
                .map(ActivityMapper::toDTO) // Kalder metode på hvert element i stream'et -> method reference i Java
                .toList(); // Returnerer DTO'er i en liste
    }

    @Override
    public Activity createActivity(Activity activity) {
        Activity newActivity = activityRepository.save(activity);

        // Default slotsgenerering for en ny aktivitet
        slotService.generateDefaultSlotsForActivity(activity.getId());

        return newActivity;
    }

    @Override
    public Activity updateActivity(Long id, Activity activity) {
        Activity toBeUpdated = activityRepository.findById(id).orElseThrow(); // Smider exception hvis ikke id findes. Så undgår jeg at skulle bruge Optional
        toBeUpdated.setName(activity.getName());
        toBeUpdated.setMinAge(activity.getMinAge());
        toBeUpdated.setMinParticipants(activity.getMinParticipants());
        toBeUpdated.setMaxParticipants(activity.getMaxParticipants());
        toBeUpdated.setDurationMinutes(activity.getDurationMinutes());
        toBeUpdated.setParallelCourts(activity.getParallelCourts());

        Activity updatedActivity = activityRepository.save(toBeUpdated);

        // Regenererer fremtidige tidsslots
        slotService.regenerateFutureSlots(updatedActivity.getId(), LocalDate.now());

        return updatedActivity;
    }

    @Override
    @Transactional
    public Activity deleteActivity(Long id) {
        Activity toBeDeleted = activityRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Activity not found: " + id));

        long bookingCount = bookingRepository.countByActivityId(id);
        if (bookingCount > 0) {
            throw new ActivityHasBookingsException("Cannot delete activity with existing bookings");
        }

        logger.info("🗑️ Deleting activity '{}' (id={})", toBeDeleted.getName(), id);
        activityRepository.delete(toBeDeleted);
        logger.info("✅ Successfully deleted activity '{}' (id={})", toBeDeleted.getName(), id);
        return toBeDeleted;
    }

}
