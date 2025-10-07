package org.example.adventurexp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationLookupDTO {

    private Long id;
    private String contactName, email, phone, customerType;
    private LocalDateTime createdAt;
    private List<BookingSummaryDTO> bookings;

}
