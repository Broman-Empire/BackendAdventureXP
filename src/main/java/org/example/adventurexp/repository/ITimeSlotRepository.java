package org.example.adventurexp.repository;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    TimeSlot findByActivity(Activity activity);

}
