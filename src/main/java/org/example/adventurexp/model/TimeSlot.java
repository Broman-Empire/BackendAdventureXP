package org.example.adventurexp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

// ------ Systemets udbudte tider for aktiviteten ------


@Entity
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation til Activity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;


    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    private int unit; // Hvilken parallelUnit dette slot repræsenterer (bane 1, 2 osv)

    // Reservationer knyttet til dette slot
    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItem> reservations = new ArrayList<>();

    public TimeSlot() {}

    public TimeSlot(Activity activity, LocalDateTime startsAt, LocalDateTime endsAt, int unit) {
        this.activity = activity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.unit = unit;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public List<ReservationItem> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationItem> reservations) {
        this.reservations = reservations;
    }

}
