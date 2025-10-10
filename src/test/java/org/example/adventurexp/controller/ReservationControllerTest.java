package org.example.adventurexp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.adventurexp.dto.CreateReservationDTO;
import org.example.adventurexp.dto.ReservationResponse;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.Equipment;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IEquipmentRepository;
import org.example.adventurexp.repository.IReservationRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IActivityRepository activityRepository;
    @Autowired
    private ITimeSlotRepository timeSlotRepository;
    @Autowired
    private IEquipmentRepository equipmentRepository;
    @Autowired
    private IReservationRepository reservationRepository;

    private Activity activity;
    private TimeSlot slot;

    @BeforeEach
    void setupTestData() {
        reservationRepository.deleteAll();
        equipmentRepository.deleteAll();
        timeSlotRepository.deleteAll();
        activityRepository.deleteAll();

            activity = new Activity();
            activity.setName("Gokart");
            activity.setMinAge(14);
            activity.setMinParticipants(1);
            activity.setMaxParticipants(10);
            activity.setDurationMinutes(60);
            activity.setParallelCourts(1);
            activityRepository.save(activity);

            slot = new TimeSlot();
            slot.setActivity(activity);
            slot.setStartsAt(LocalDateTime.now().plusDays(1));
            slot.setEndsAt(LocalDateTime.now().plusDays(1).plusHours(1));
            slot.setCapacity(10);
            slot.setCourt(1);
            timeSlotRepository.save(slot);

        Equipment eq = new Equipment();
        eq.setActivity(activity);
        eq.setUsableSets(10);
        equipmentRepository.save(eq);
    }

    @AfterEach
    void cleanUp() {
        reservationRepository.deleteAll();
        equipmentRepository.deleteAll();
        timeSlotRepository.deleteAll();
        activityRepository.deleteAll();
    }

//    @Test
//    void testCreateAndGetReservation() throws Exception {
//        CreateReservationDTO createDto = new CreateReservationDTO();
//        createDto.setCustomerType("PRIVATE");
//        createDto.setContactName("Pølsemand Jens");
//        createDto.setEmail("poelse@jens.dk");
//        createDto.setPhone("12345678");
//        createDto.setActivityId(activity.getId());
//        createDto.setParticipants(2);
//        createDto.setGroupMinAge(18);
//        createDto.setSlotId(slot.getId());
//
//        // Create reservation
//        MvcResult postResult = mockMvc.perform(
//                post("/reservations")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createDto)) // objectMapper converts DTO to JSON
//        )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").exists())
//                .andReturn();
//
//        String body = postResult.getResponse().getContentAsString(); // get response body
//        ReservationResponse response = objectMapper.readValue(body, ReservationResponse.class); // convert JSON to DTO
//        Long reservationId = response.getId();
//
//        // Get reservation by ID
//        mockMvc.perform(get("/reservations/{id}", reservationId))
//                .andExpect(jsonPath("$.id").value(reservationId))
//                .andExpect(jsonPath("$.participants").value(2));
//    }
}
