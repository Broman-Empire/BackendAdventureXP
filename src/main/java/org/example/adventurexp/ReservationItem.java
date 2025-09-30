package org.example.adventurexp;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Har relation til Reservation og Activity

@Entity
public class ReservationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation til Reservation
    @ManyToOne(fetch = FetchType.LAZY) // ReservationItem afhænger af Reservation, og hentes kun hvis den kaldes i koden
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // Relation til Activity
    @ManyToOne(fetch = FetchType.LAZY) // ReservationItem afhænger af Activity og hentes kun, når den kaldes
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    private LocalDateTime startsAt, endsAt;

    private int participants;

    public ReservationItem(Reservation reservation, Activity activity, LocalDateTime startsAt, LocalDateTime endsAt, int participants) {
        this.reservation = reservation;
        this.activity = activity;
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

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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
