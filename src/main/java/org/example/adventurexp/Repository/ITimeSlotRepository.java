package org.example.adventurexp.repository;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ITimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    Optional<List<TimeSlot>> findByActivity(Activity activity);

    Optional<TimeSlot> findByActivityAndStartsAt(Activity activity, LocalDateTime startsAt);

}
