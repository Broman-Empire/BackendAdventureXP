package org.example.adventurexp.service;

import org.example.adventurexp.dto.AvailabilityDTO;
import java.time.LocalDate;

public interface IAvailabilityService {

    // Returner dagens tilgængelige slots for en aktivitet
    AvailabilityDTO[] getDailyAvailability(long activityId, LocalDate date);

}


