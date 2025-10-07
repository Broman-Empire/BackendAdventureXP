package org.example.adventurexp.service;

import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.mapper.ActivityMapper;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IBookingRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.springframework.stereotype.Service;

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

    public ActivityService(IActivityRepository activityRepository, IBookingRepository bookingRepository) {
        this.activityRepository = activityRepository;
        this.bookingRepository = bookingRepository;
    }

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
        return activityRepository.save(activity);
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
        return activityRepository.save(toBeUpdated);
    }

    @Override
    public Activity deleteActivity(Long id) {
        Activity toBeDeleted = activityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Activity not found: " + id));

        long bookingCount = bookingRepository.countByActivityId(id);
        if (bookingCount > 0) {
            throw new IllegalArgumentException("Cannot delete activity with existing bookings");
        }
        activityRepository.delete(toBeDeleted);
        return toBeDeleted;
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

        slotService.generateSlots(activityId, fromDate, toDate, openTime, closeTime);
    }

}
