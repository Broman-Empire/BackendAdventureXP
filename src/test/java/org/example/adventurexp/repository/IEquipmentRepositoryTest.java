package org.example.adventurexp.repository;

import org.example.adventurexp.model.Equipment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EquipmentRepositoryIT {

    @Autowired
    private IEquipmentRepository equipmentRepository;

    @Test
    @DisplayName("Test af findById(Long id)")
    void testFindById() {
        // Arrange
        long equipmentId = 1L; // Gokart sæt from import.sql

        // Act
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);

        // Assert
        assertThat(equipmentOpt).isPresent();
        Equipment equipment = equipmentOpt.get();
        assertThat(equipment.getName()).isEqualTo("Gokart sæt");
        assertThat(equipment.getTotalSets()).isEqualTo(12);
        assertThat(equipment.getUsableSets()).isEqualTo(12);
    }

    @Test
    @DisplayName("Test af findByActivityId(Long activityId)")
    void testFindByActivityId() {
        // Arrange
        long activityId = 1L; // Gokart activity

        // Act
        List<Equipment> equipmentList = equipmentRepository.findByActivityId(activityId);

        // Assert
        assertThat(equipmentList).hasSize(1);
        assertThat(equipmentList.get(0).getName()).isEqualTo("Gokart sæt");
    }

    @Test
    @DisplayName("Test af save(Equipment equipment)")
    void testSaveEquipment() {
        // Arrange
        long minigolfActivityId = 3L; // Minigolf
        Equipment newEquipment = new Equipment();
        newEquipment.setName("New Minigolf Set");
        newEquipment.setTotalSets(10);
        newEquipment.setUsableSets(10);
        newEquipment.setActivity(equipmentRepository.findByActivityId(minigolfActivityId).get(0).getActivity());

        // Act
        Equipment saved = equipmentRepository.save(newEquipment);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Minigolf Set");

        Optional<Equipment> found = equipmentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTotalSets()).isEqualTo(10);
    }

    @Test
    @DisplayName("Test af deleteById(Long id)")
    void testDeleteEquipment() {
        // Arrange
        long equipmentId = 1L; // Gokart sæt
        Equipment equipment = equipmentRepository.findById(equipmentId).get();

        // Act
        equipmentRepository.deleteById(equipment.getId());

        // Assert
        Optional<Equipment> deleted = equipmentRepository.findById(equipmentId);
        assertThat(deleted).isEmpty();
    }
}