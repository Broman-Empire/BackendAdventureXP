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

    // Find items for en aktivitet i et tidsrum
    List<ReservationItem> findByActivityIdAndTimeRange(Long activityId, LocalDateTime from, LocalDateTime to);

}
