package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IEquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.stream;

@Service
public class EquipmentServiceImpl implements IEquipmentService {

    private final IEquipmentRepository equipmentRepository;
    private final IActivityRepository activityRepository; // Assuming you have an ActivityRepository

    public EquipmentServiceImpl(IEquipmentRepository equipmentRepository, IActivityRepository activityRepository) {
        this.equipmentRepository = equipmentRepository;
        this.activityRepository = activityRepository;
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

    @Override
    public List<Equipment> listByActivity(long activityId) {
        return getEquipmentByActivityId(activityId);
    }

    @Override
    public void addEquipment(long activityId, Equipment equipment) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));

        //check for dupplicates
        List<Equipment> existingEquipment = equipmentRepository.findByActivityId(activityId)
                .stream()
                .filter(e -> e.getName().equalsIgnoreCase(equipment.getName()))
                .toList();
        if(!existingEquipment.isEmpty()) {
            return; // Duplicate found, do not add
        }
        equipment.setActivity(activity);
        equipmentRepository.save(equipment);
    }
}