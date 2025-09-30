package org.example.adventurexp.service;

import org.example.adventurexp.repository.ReservationItemRepository;
import org.example.adventurexp.repository.ReservationRepository;
import org.example.adventurexp.model.ReservationItem;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationItemRepository reservationItemRepository;

    public ReservationService(ReservationRepository reservationRepository, ReservationItemRepository reservationItemRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationItemRepository = reservationItemRepository;
    }

    public void ensureNoOverlaps(List<ReservationItem> items) {
        if(items == null || items.isEmpty()) return;

        for (ReservationItem item : items) {
            if(item == null) continue;
            LocalDateTime start = item.getStartsAt();
            LocalDateTime end = item.getEndsAt();
            if(start == null || end == null) {
                throw new IllegalArgumentException("ReservationItem has null start or end time");
            }
            if(!end.isAfter(start)) {
                throw new IllegalArgumentException("ReservationItem end time must be after start time");
            }
        }

        List<ReservationItem> sorted = items.stream()
                .filter(i -> i != null) // we filter null values out
                .sorted(Comparator
                        .comparing(ReservationItem::getStartsAt) // first, we sort by start time
                        .thenComparing(ReservationItem::getEndsAt)) // if start time is same, sort by end time
                .toList();

        LocalDateTime lastEnd = null;
        for (ReservationItem item : sorted) {
            if (lastEnd != null && item.getStartsAt().isBefore(lastEnd)) { // if the current start is before the last end
                throw new IllegalArgumentException("Overlapping reservation items detected");
            }
            lastEnd = item.getEndsAt(); // update lastEnd to current activity end
        }
    }

//    public void validateReservation(Object request) {
    /** TODO:
     *  - Check age requirements:
     *      * COMPANY: age >= 16
     *      * PRIVATE: per activity minAge
     *  - Ensure participants > 0
     *  - Ensure slot exists
     *  - Ensure remaining capacity >= participants
     *  - Ensure participants <= usableSets
     *  Parameter: request (replace Object with CreateReservationRequest when available)
     */
//    }

}
