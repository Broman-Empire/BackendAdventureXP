package org.example.adventurexp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "equipment")

public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int totalSets, usableSets;

    @ManyToOne(fetch = FetchType.LAZY) // Henter kun Equipment, når den kaldes (fordi den relateres til Activity)
    @JoinColumn(name = "activity_id", referencedColumnName = "id", nullable = false) // Foreign key kolonne i DB
    private Activity activity; // FK kommer fra Activity


    public Equipment(String name, int totalSets, int usableSets) {
        this.name = name;
        this.totalSets = totalSets;
        this.usableSets = usableSets;
    }

    public Equipment() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalSets() {
        return totalSets;
    }

    public void setTotalSets(int totalSets) {
        this.totalSets = totalSets;
    }

    public int getUsableSets() {
        return usableSets;
    }

    public void setUsableSets(int usableSets) {
        this.usableSets = usableSets;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }


}
