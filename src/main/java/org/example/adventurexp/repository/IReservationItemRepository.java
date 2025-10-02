package org.example.adventurexp.repository;


import org.example.adventurexp.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IReservationItemRepository extends JpaRepository<Booking, Long> {
    //Find by reservationID
    List<Booking> findByReservationId(Long reservationId);

    //Find items between two start times
    List<Booking> findByStartsAtBetween(LocalDateTime from, LocalDateTime to);
}
