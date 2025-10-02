package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.repository.IEquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl implements IEquipmentService {

    private final IEquipmentRepository equipmentRepository;

    public EquipmentServiceImpl(IEquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    // Implements interface method
    @Override
    public List<Equipment> getEquipmentByActivityId(long activityId) {
        return equipmentRepository.findByActivityId(activityId);
    }

    // Return usableSets for an activity
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