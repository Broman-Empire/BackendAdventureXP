package org.example.adventurexp.repositories;

import org.example.adventurexp.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEquipmentRepository extends JpaRepository<Equipment, Integer>{
    @Override
    List<Equipment> findByActivityId(int activityId);
}
