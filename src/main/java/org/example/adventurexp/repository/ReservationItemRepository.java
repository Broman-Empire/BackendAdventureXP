package org.example.adventurexp.repository;


import org.example.adventurexp.model.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long> {
    //Find by reservationID
    List<ReservationItem> findByReservationId(Long reservationId);

    //Find items between two start times
    List<ReservationItem> findByStartsAtBetween(LocalDateTime from, LocalDateTime to);
}
