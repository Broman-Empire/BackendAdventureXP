package org.example.adventurexp.repository;

import org.example.adventurexp.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    Reservation findByReservationId(Long reservationId);

    List<Reservation> findByCustomerPhoneContaining(String phonenumber);
}