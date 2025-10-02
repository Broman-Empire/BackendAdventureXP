package org.example.adventurexp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// ------ En konkret booking af et slot for x antal deltagere ------

// Har relation til Reservation og TimeSlot

@Entity
public class ReservationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation til Reservation = kunden
    @ManyToOne(fetch = FetchType.LAZY) // ReservationItem afhænger af Reservation, og hentes kun hvis den kaldes i koden
    @JoinColumn(name = "reservation_id", referencedColumnName = "id", nullable = false)
    private Reservation reservation;

    // Relation til TimeSlot = aktivitetens konkrete tid
    @ManyToOne(fetch = FetchType.LAZY) // ReservationItem afhænger af TimeSlot og hentes kun, når den kaldes
    @JoinColumn(name = "timeslot_id", nullable = false)
    private TimeSlot timeSlot;

    //Relation til Activity
    @ManyToOne
    @JoinColumn

    //Todo: Slet da den findes i TimeSlot kh Sofie
    private LocalDateTime startsAt, endsAt;

    private int participants;

    public ReservationItem(Reservation reservation, TimeSlot timeSlot, LocalDateTime startsAt, LocalDateTime endsAt, int participants) {
        this.reservation = reservation;
        this.timeSlot = timeSlot;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.participants = participants;
    }

    public ReservationItem() {
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }
}
