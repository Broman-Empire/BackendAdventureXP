package org.example.adventurexp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

// ------ Systemets udbudte tider for aktiviteten ------


@Entity
@Table(name = "time_slot")

public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation til Activity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Activity activity;


    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    private int court; // Hvilken parallelCourt dette slot repræsenterer (bane 1, 2 osv)



    /**
     * Capacity angiver hvor mange deltagere der maksimalt kan bookes i dette slot.
     * Beregnes typisk ud fra aktivitetens maxParticipants * parallelUnits.
     * Brugt af AvailabilityService til at beregne remaining = min(capacity, usableSets) - reservedCount.
     */
    private int capacity;

    // Reservationer knyttet til dette slot
    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();



    public TimeSlot() {}

    public TimeSlot(Activity activity, LocalDateTime startsAt, LocalDateTime endsAt, int court) {
        this.activity = activity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.court = court;
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

    public int getCourt() {
        return court;
    }

    public void setCourt(int court) {
        this.court = court;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBooking(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
