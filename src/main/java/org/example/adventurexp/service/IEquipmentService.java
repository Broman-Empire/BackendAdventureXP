package org.example.adventurexp.service;

import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.repository.IEquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IEquipmentService {

    List<Equipment> getEquipmentByActivityId(long activityId);

    int usableSets(Activity activity);

    List<Equipment> listByActivity(long activityId);

    void addEquipment(long activityId, Equipment equipment);
}
