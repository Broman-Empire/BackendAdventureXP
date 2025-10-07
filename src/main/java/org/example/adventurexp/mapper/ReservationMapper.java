package org.example.adventurexp.mapper;

import org.example.adventurexp.dto.BookingSummaryDTO;
import org.example.adventurexp.dto.ReservationLookupDTO;
import org.example.adventurexp.model.Booking;
import org.example.adventurexp.model.Reservation;
import org.example.adventurexp.model.TimeSlot;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationMapper {

    public static ReservationLookupDTO toLookupDTO(Reservation reservation) {
        if (reservation == null) return null;

        ReservationLookupDTO dto = new ReservationLookupDTO();
        dto.setId(reservation.getId());
        dto.setContactName(reservation.getContactName());
        dto.setEmail(reservation.getEmail());
        dto.setPhone(reservation.getPhone());
        dto.setCustomerType(reservation.getCustomerType());
        dto.setCreatedAt(reservation.getCreatedAt());

        dto.setBookings(reservation.getBookings().stream()
                .map(ReservationMapper::toBookingSummaryDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private static BookingSummaryDTO toBookingSummaryDTO(Booking booking) {
        if (booking == null) return null;

        BookingSummaryDTO dto = new BookingSummaryDTO();
        dto.setActivityName(booking.getActivity().getName());

        TimeSlot timeSlot = booking.getTimeSlot();
        if (timeSlot != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
            dto.setTimeSlot(timeSlot.getStartsAt().format(fmt) + " - " + timeSlot.getEndsAt().format(fmt));
        }

        return dto;
    }

    public static List<ReservationLookupDTO> toLookupDTOList(List<Reservation> reservations) {

        return reservations.stream()
                .map(ReservationMapper::toLookupDTO)
                .collect(Collectors.toList());
    }
}
