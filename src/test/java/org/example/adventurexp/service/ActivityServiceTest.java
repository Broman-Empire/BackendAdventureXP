package org.example.adventurexp.service;

import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.mapper.ActivityMapper;
import org.example.adventurexp.model.Activity;
import org.example.adventurexp.model.TimeSlot;
import org.example.adventurexp.repository.IActivityRepository;
import org.example.adventurexp.repository.IBookingRepository;
import org.example.adventurexp.repository.ITimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityServiceTest {

    // Mocker de nødvendige repositories
    @Mock
    private IActivityRepository activityRepository;
    @Mock
    private IBookingRepository bookingRepository;
    @Mock
    private ITimeSlotRepository timeSlotRepository;

    // Mocker den nødvendige service
    @Mock
    private ISlotService slotService;

    // Injecter mocks i ActivityService
    @InjectMocks
    private ActivityService activityService;

    // Initialiserer mocks før hver test køres
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test af findAll()")
    void findAll() {
        // Arrange
        Activity gokart = new Activity(1L, "Gokart", 12, 1, 12, 15, 5);
        Activity paintball = new Activity(2L, "Paintball", 16, 4, 20, 90, 2);

        // Når vi kalder findAll(), så returnerer vi en liste med de ovenstående aktiviteter
        when(activityRepository.findAll()).thenReturn(Arrays.asList(gokart, paintball));

        // Act
        List<ActivityDTO> allActivities = activityService.findAll();

        // Assert
        assertThat(allActivities).hasSize(2); // Tjekker listens størrelse
        assertThat(allActivities.get(0).getName()).isEqualTo("Gokart"); // Tjekker navn på første aktivitet
        verify(activityRepository, times(1)).findAll(); // Tjekker at findAll() blev kaldt 1 gang
    }

    @Test
    @DisplayName("Test af createActivity(Activity activity)")
    void createActivity() {
        // Arrange
        Activity newActivity = new Activity(null, "Batting cage", 12, 1, 2, 60, 4);
        Activity savedActivity = new Activity(5L, "Batting cage", 12, 1, 2, 60, 4);

        // Når vi kalder save() på repository'et med newActivity, så returnerer vi savedActivity
        when(activityRepository.save(newActivity)).thenReturn(savedActivity);

        // Act
        Activity result = activityService.createActivity(newActivity);

        // Assert
        assertThat(result.getId()).isNotNull(); // Tjekker at ID ikke er null
        assertThat(result.getName()).isEqualTo("Batting cage"); // Tjekker navn
        verify(activityRepository, times(1)).save(newActivity); // Tjekker at save() blev kaldt 1 gang med newActivity
        verify(slotService, times(1)).generateDefaultSlotsForActivity(savedActivity.getId()); // Tjekker at slots blev genereret

    }

    @Test
    void updateActivity() {
    }

    @Test
    void deleteActivity() {
    }

    @Test
    void regenerateFutureSlots() {
    }
}