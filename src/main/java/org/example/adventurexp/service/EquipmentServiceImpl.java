package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IBookingRepository;
import org.example.adventurexp.repository.IEquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.stream;

@Service
public class EquipmentServiceImpl implements IEquipmentService {

    private final IEquipmentRepository equipmentRepository;
    private final IActivityRepository activityRepository;
    private final IBookingRepository bookingRepository;

    public EquipmentServiceImpl(IEquipmentRepository equipmentRepository, IActivityRepository activityRepository, IBookingRepository bookingRepository) {
        this.equipmentRepository = equipmentRepository;
        this.activityRepository = activityRepository;
        this.bookingRepository = bookingRepository;
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

        //check for duplicates
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

    @Override
    public Equipment updateEquipment(long equipmentId, Equipment patch) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + equipmentId));

        if(patch.getName() != null) {
            equipment.setName(patch.getName());
        }
        if(patch.getTotalSets() != 0) {
            equipment.setTotalSets(patch.getTotalSets());
        }
        if(patch.getUsableSets() != 0) {
            equipment.setUsableSets(patch.getUsableSets());
        }
        equipmentRepository.save(equipment);
        return equipment;
    }

    @Override
    public void deleteEquipment(long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + equipmentId));
        long activityId = equipment.getActivity().getId();

        if(bookingRepository.existsByActivity_Id(activityId)) {
            throw new IllegalArgumentException("Cannot delete equipment: There are bookings associated with this activity.");
        }
        equipmentRepository.deleteById(equipmentId);
    }
}