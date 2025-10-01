package org.example.adventurexp.service;

import org.example.adventurexp.dto.AvailabilityDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    @Override
    public AvailabilityDTO[] getDailyAvailability(long activityId, LocalDate date) {
        // returnerer et tomt array for nu
        return new AvailabilityDTO[0];
    }
}