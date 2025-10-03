package org.example.adventurexp.repository;


import org.example.adventurexp.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
    //Find by reservationID
    List<Booking> findByReservationId(Long reservationId);

    
	//Find bookings for en aktivitet i et tidsinterval
	List<Booking> findByActivityIdAndTimeSlot(Long activityId, LocalDateTime from, LocalDateTime to);

    //Find items between two start times
    List<Booking> findByStartsAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("""
           select coalesce(sum(b.participants), 0)
           from Booking b
           where b.activity.id = :activityId
             and b.timeSlot.id = :timeSlotId
           """)
    int sumParticipantsByActivityAndSlot(@Param("activityId") Long activityId,
                                         @Param("timeSlotId") Long timeSlotId);
}


