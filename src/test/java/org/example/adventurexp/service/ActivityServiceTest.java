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
        // Siden vi ikke rigtig har noget Hibernate med her, så kan den ikke finde ud af, at generere ID korrekt
        //Activity newActivity = new Activity(null, "Batting cage", 12, 1, 2, 60, 4);
        Activity savedActivity = new Activity(5L, "Batting cage", 12, 1, 2, 60, 4);

        // Når vi kalder save() på repository'et med newActivity, så returnerer vi savedActivity
        when(activityRepository.save(savedActivity)).thenReturn(savedActivity);

        // Act
        Activity result = activityService.createActivity(savedActivity);

        // Assert
        assertThat(result.getId()).isNotNull(); // Tjekker at ID ikke er null
        assertThat(result.getName()).isEqualTo("Batting cage"); // Tjekker navn på aktiviteten
        verify(activityRepository, times(1)).save(savedActivity); // Tjekker at save() blev kaldt 1 gang med newActivity
        // Hernede lavede den ballade når jeg kørte dobbelt med newActivity og savedActivity, da den jo ikke genererede et nyt ID til den nye Activity
        verify(slotService, times(1)).generateDefaultSlotsForActivity(savedActivity.getId()); // Tjekker at slots blev genereret

    }

    @Test
    @DisplayName("Test af updateActivity(Long id, Activity activity)")
    void updateActivity() {
        // Arrange
        Long activityId = 1L;
        Activity existingActivity = new Activity(activityId, "Gokart", 12, 1, 12, 15, 5);
        Activity updatedActivity = new Activity(activityId, "Gokart", 12, 1, 10, 15, 6);

        // Når vi kalder findById() med activityId, så returnerer vi existingActivity
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(existingActivity));
        // Når vi kalder save() med en vilkårlig Activity-instans, så returnerer vi updatedActivity
        when(activityRepository.save(any(Activity.class))).thenReturn(updatedActivity);

        // Act
        Activity result = activityService.updateActivity(activityId, updatedActivity);

        // Assert
        assertThat(result.getMaxParticipants()).isEqualTo(10); // Tjekker at maxParticipants er sat ned til 10
        assertThat(result.getParallelCourts()).isEqualTo(6); // Tjekker at parallelCourts er sat op til 6
        verify(activityRepository, times(2)).findById(activityId); // Tjekker at findById() blev kaldt 2 gange (1 i updateActivity og 1 i regenerateFutureSlots)
        verify(activityRepository, times(1)).save(any(Activity.class)); // Tjekker at save() blev kaldt 1 gang
        // regenerateFutureSlots ligger i ActivityService og ikke i SlotService :thinko:
        //verify(activityService, times(1)).regenerateFutureSlots(activityId, LocalDate.now()); // Tjekker at fremtidige slots blev genereret
    }

    @Test
    @DisplayName("Test af deleteActivity(Long id) - succes")
    void deleteActivityNoBookings() {
        // Arrange
        Long activityId = 1L;
        Activity existingActivity = new Activity(activityId, "Gokart", 12, 1, 12, 15, 5);

        // Når vi kalder findById() med activityId, så returnerer vi existingActivity
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(existingActivity));
        // Når vi kalder countByActivityId() med activityId, så returnerer vi 0
        when(bookingRepository.countByActivityId(activityId)).thenReturn(0L);

        // Act
        Activity result = activityService.deleteActivity(activityId);

        // Assert
        assertThat(result.getId()).isEqualTo(activityId); // Tjekker at den returnerede aktivitet har det rigtige ID
        verify(activityRepository, times(1)).findById(activityId); // Tjekker at findById() blev kaldt 1 gang
        verify(bookingRepository, times(1)).countByActivityId(activityId); // Tjekker at countByActivityId() blev kald
    }

    @Test
    void deleteActivityWithBookings() {
    // Arrange
        Long activityId = 1L;
        Activity existingActivity = new Activity(activityId, "Gokart", 12, 1, 12, 15, 5);

        // Når vi kalder findById() med activityId, så returnerer vi existingActivity
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(existingActivity));
        // Når vi kalder countByActivityId() med activityId, så returnerer vi 5 (simulerer at der er eksisterende bookinger)
        when(bookingRepository.countByActivityId(activityId)).thenReturn(5L);

        // Act & Assert
        assertThatThrownBy(() -> activityService.deleteActivity(activityId))
                .isInstanceOf(IllegalArgumentException.class) // Jf. den exception der kastes i ActivityService
                .hasMessageContaining("Cannot delete activity with existing bookings"); // Jf. den besked der kastes i ActivityService

        verify(activityRepository, times(1)).findById(activityId); // Tjekker at findById() blev kaldt 1 gang
        verify(bookingRepository, times(1)).countByActivityId(activityId); // Tjekker at countByActivityId() blev kaldt 1 gang
    }
}