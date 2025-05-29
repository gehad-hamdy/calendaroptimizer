package com.calendaroptimizer.dto;

import com.calendaroptimizer.entities.Booking;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverlappingBookingDTO {
    private Long booking1Id;
    private Long booking2Id;
    private String propertyTitle;
    private LocalDate overlapStart;
    private LocalDate overlapEnd;

    public OverlappingBookingDTO(Booking b1, Booking b2, String propertyTitle) {
        this.booking1Id = b1.getId();
        this.booking2Id = b2.getId();
        this.propertyTitle = propertyTitle;
        this.overlapStart = b1.getStartDate().isAfter(b2.getStartDate()) ?
            b1.getStartDate() : b2.getStartDate();
        this.overlapEnd = b1.getEndDate().isBefore(b2.getEndDate()) ?
            b1.getEndDate() : b2.getEndDate();
    }
}