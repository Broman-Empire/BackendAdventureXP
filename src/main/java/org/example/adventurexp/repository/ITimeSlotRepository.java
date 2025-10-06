package org.example.adventurexp.repository;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//Underscore (_) er Spring Data JPA’s måde at “navigere” mellem entitetsrelationer på.


public interface ITimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByActivity(Activity activity);

    // Til generateSlots() og createIfNotExists()
    Optional<TimeSlot> findByActivityAndStartsAtAndCourt(Activity activity, LocalDateTime startsAt, int court);

    // For getAvailableSlots()
    List<TimeSlot> findByActivityAndStartsAtAfter(Activity activity, LocalDateTime startsAt);

}