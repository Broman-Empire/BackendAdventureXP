package org.example.adventurexp.repository;

import org.example.adventurexp.model.Activity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class IActivityRepositoryTest {

    @Autowired
    private IActivityRepository activityRepository;

    @Test
    @DisplayName("Test af findByName(String name)")
    void testFindByName() {
        // Arrange
        String nameToFind = "Gokart";

        // Act
        Optional<Activity> found = activityRepository.findByName("Gokart");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(nameToFind);
        assertThat(found.get().getMaxParticipants()).isEqualTo(12);
    }

    @Test
    @DisplayName("Test af save()")
    void testSaveActivity() {
        Activity activity = new Activity(null, "Batting cage", 12, 1, 2, 60, 4);
        Activity saved = activityRepository.save(activity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Batting cage");
        assertThat(saved.getMaxParticipants()).isEqualTo(2);
        assertThat(saved.getName()).isEqualTo(activityRepository.findById(5L).get().getName());
    }
}
