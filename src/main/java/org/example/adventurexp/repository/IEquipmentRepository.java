package org.example.adventurexp.repository;

import org.example.adventurexp.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//Underscore (_) er Spring Data JPA’s måde at “navigere” mellem entitetsrelationer på.


public interface IEquipmentRepository extends JpaRepository<Equipment, Long>{

    // Returnerer en liste af Equipment ud fra ActivityID
    List<Equipment> findByActivityId(long activityId);
}
