package org.example.adventurexp.model;

import jakarta.persistence.*;

// ------ En konkret booking af et slot for x antal deltagere ------

// Har relation til Reservation og TimeSlot

@Entity
@Table(name = "booking")

public class Booking {
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false) // Vi har ikke referencedColumnName, da den leder efter "id"
    Activity activity;                                  // by default. Hed vores PK noget andet, ville vi bruge det.

    public void setId(Long id) {
        this.id = id;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private int participants;

    public Booking(Reservation reservation, TimeSlot timeSlot, int participants) {
        this.reservation = reservation;
        this.timeSlot = timeSlot;
        this.participants = participants;
    }

    public Booking() {
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

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }
}
