package org.example.adventurexp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int minAge;
    private int minParticipants;
    private int maxParticipants;
    private int durationMinutes;
    private int parallelCourts;

    public Activity() {
    }

    public Activity(Long id, String name, int minAge, int minParticipants, int maxParticipants, int durationMinutes, int parallelCourts) {
        this.id = id;
        this.name = name;
        this.minAge = minAge;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.durationMinutes = durationMinutes;
        this.parallelCourts = parallelCourts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMinParticipants() {
        return minParticipants;
    }

    public void setMinParticipants(int minParticipants) {
        this.minParticipants = minParticipants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getParallelCourts() {
        return parallelCourts;
    }

    public void setParallelCourts(int parallelUnits) {
        this.parallelCourts = parallelUnits;
    }

}
