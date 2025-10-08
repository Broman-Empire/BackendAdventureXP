package org.example.adventurexp.repository;

import org.example.adventurexp.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

//Underscore (_) er Spring Data JPA’s måde at “navigere” mellem entitetsrelationer på.


public interface IReservationRepository extends JpaRepository<Reservation, Long> {


    List<Reservation> findByPhone(String phone);

    @Query("""
        SELECT DISTINCT r FROM Reservation r
        JOIN r.bookings b
        JOIN b.timeSlot t
        WHERE DATE(t.startsAt) = :date
    """)
    List<Reservation> findByBookingDate(LocalDate date);
}