package org.example.adventurexp.Repository;

import org.example.adventurexp.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IEquipmentRepository extends JpaRepository<Equipment, Long>{

    // Returnerer en liste af Equipment ud fra ActivityID
    List<Equipment> findByActivityId(long activityId);
}
