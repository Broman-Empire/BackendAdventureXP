package org.example.adventurexp.model;


public class Activity {

    private Long id;
    private String name;
    private int minAge;
    private int minParticipants;
    private int maxParticipants;
    private int durationMinutes;
    private int parallelUnits;
    private String equipmentRef;

    public Activity() {
    }

    public Activity(Long id, String name, int minAge, int minParticipants, int maxParticipants, int durationMinutes, int parallelUnits, String equipmentRef) {
        this.id = id;
        this.name = name;
        this.minAge = minAge;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.durationMinutes = durationMinutes;
        this.parallelUnits = parallelUnits;
        this.equipmentRef = equipmentRef;
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

    public int getParallelUnits() {
        return parallelUnits;
    }

    public void setParallelUnits(int parallelUnits) {
        this.parallelUnits = parallelUnits;
    }

    public String getEquipmentRef() {
        return equipmentRef;
    }

    public void setEquipmentRef(String equipmentRef) {
        this.equipmentRef = equipmentRef;
    }


}
