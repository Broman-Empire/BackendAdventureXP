package org.example.adventurexp.service;

import org.example.adventurexp.model.Equipment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    org.example.adventurexp.repository.ITimeSlotRepository timeSlotRepository;
    @Mock
    IEquipmentService equipmentService;
    @Mock
    IReservationService reservationService;

    @InjectMocks
    AvailabilityServiceImpl sut;

    @Test
    void activeEquipmentSets_sumsUsableSets() {
        long activityId = 42L;
        var e1 = new Equipment();
        e1.setUsableSets(3);
        var e2 = new Equipment();
        e2.setUsableSets(5);
        when(equipmentService.getEquipmentByActivityId(activityId))
                .thenReturn(List.of(e1, e2));

        assertEquals(8, sut.activeEquipmentSets(activityId));
    }

    @Test
    void activeEquipmentSets_noEquipment_returnsZero() {
        when(equipmentService.getEquipmentByActivityId(anyLong()))
                .thenReturn(Collections.emptyList());
        assertEquals(0, sut.activeEquipmentSets(1L));
    }
}


