package org.example.adventurexp.repositories;

import org.example.adventurexp.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IEquipmentRepository extends JpaRepository<Equipment, Integer>{

    // Returnerer en liste af Equipment ud fra ActivityID
    List<Equipment> findByActivityId(int activityId);
}
