package org.example.adventurexp.controller;
import org.example.adventurexp.controller.ActivityController;
import org.example.adventurexp.dto.ActivityDTO;
import org.example.adventurexp.service.IActivityService;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityControllerTest {

//    @Test
//    void getAllActivities() {
//        // Arrange
//        IActivityService mockService = mock(IActivityService.class);
//        List<ActivityDTO> expected = List.of(
//                new ActivityDTO(1L, "Gokart", 14, 120),
//                new ActivityDTO(2L, "Sumobrydning", 10, 60)
//        );
//        when(mockService.findAll()).thenReturn(expected);
//
//        ActivityController controller = new ActivityController(mockService);
//
//        // Act
//        List<ActivityDTO> result = controller.getAllActivities();
//
//        // Assert
//        assertEquals(expected, result);
//        verify(mockService).findAll();
//    }
}
