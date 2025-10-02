package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.repository.IEquipmentRepository;

import java.util.List;

public class EquipmentService {

    private final IEquipmentRepository equipmentRepository;

    public EquipmentService(IEquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    // Returner usableSets for en aktivitet
    public int usableSets(Activity activity) {
        Long activityId = activity.getId();
        List<Equipment> equipmentList = equipmentRepository.findByActivityId(activityId);
        if (equipmentList == null || equipmentList.isEmpty()) {
            return 0;
        } else {
            return equipmentList.stream().mapToInt(Equipment::getUsableSets).sum();
        }
    }
}
