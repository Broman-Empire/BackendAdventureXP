package org.example.adventurexp.Repository;

import org.example.adventurexp.model.Reservation;
import org.kea.adventurexp.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    Reservation findByReservationId(Long reservationId);


}