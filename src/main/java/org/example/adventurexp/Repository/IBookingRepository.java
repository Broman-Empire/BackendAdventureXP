package org.example.adventurexp.repository;


import org.example.adventurexp.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
    //Find by reservationID
    List<Booking> findByReservationId(Long reservationId);

    //Find bookings for en aktivitet i et tidsinterval
    List<Booking> findByActivityIdAndTimeSlot(Long activityId, LocalDateTime from, LocalDateTime to);
}
