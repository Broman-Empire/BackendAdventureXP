package org.example.adventurexp.repository;

import org.example.adventurexp.model.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class IReservationRepositoryIT { // IT står for Integration Test

    @Autowired
    private IReservationRepository reservationRepository;

    @Test
    @DisplayName("Test af findById(Long id)")
    void testFindById() {
        // Arrange
        long existingReservationId = 1L; // Sofie Hansen from import.sql

        // Act
        Optional<Reservation> reservationOpt = reservationRepository.findById(existingReservationId);

        // Assert
        assertThat(reservationOpt).isPresent();
        Reservation reservation = reservationOpt.get();
        assertThat(reservation.getContactName()).isEqualTo("Sofie Hansen");
        assertThat(reservation.getPhone()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("Test af findByPhone(String phone)")
    void testFindByPhone() {
        // Arrange
        String phoneNumber = "12345678";

        // Act
        List<Reservation> reservations = reservationRepository.findByPhone(phoneNumber);

        // Assert
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getContactName()).isEqualTo("Sofie Hansen");
    }

    @Test
    @DisplayName("Save a new reservation")
    void testSaveReservation() {
        // Arrange
        Reservation newReservation = new Reservation();
        newReservation.setCustomerType("Private");
        newReservation.setContactName("John Doe");
        newReservation.setEmail("john@example.com");
        newReservation.setPhone("55512345");
        newReservation.setCreatedAt(java.time.LocalDateTime.now());

        // Act
        Reservation saved = reservationRepository.save(newReservation);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContactName()).isEqualTo("John Doe");

        Optional<Reservation> found = reservationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }
}
