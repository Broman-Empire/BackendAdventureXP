package org.example.adventurexp.repository;

import org.example.adventurexp.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IActivityRepository extends JpaRepository<Activity, Long> {


    // Spring Data JPA laver automatik en query baseret på navnet
    Optional<Activity> findByName(String name);

}
