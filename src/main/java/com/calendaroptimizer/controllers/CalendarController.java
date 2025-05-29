package com.calendaroptimizer.controllers;

import com.calendaroptimizer.dto.BookingRequest;
import com.calendaroptimizer.dto.CalendarRequest;
import com.calendaroptimizer.dto.ConflictResponse;
import com.calendaroptimizer.dto.OverlappingBookingDTO;
import com.calendaroptimizer.dto.TopUserDTO;
import com.calendaroptimizer.entities.Booking;
import com.calendaroptimizer.entities.Property;
import com.calendaroptimizer.services.CalendarService;
import com.calendaroptimizer.services.PropertyBookingService;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    private final PropertyBookingService bookingService;

    public CalendarController(final CalendarService calendarService, final PropertyBookingService bookingService) {
        this.calendarService = calendarService;
        this.bookingService = bookingService;
    }

    @PostMapping("/analyze")
    public ConflictResponse analyzeCalendar(@Valid @RequestBody final CalendarRequest request) {
        return calendarService.analyzeCalendar(request);
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody final BookingRequest request) {
        if (!request.isValidDateRange()) {
            throw new BadRequestException("End date must be after start date");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookingService.createBooking(request));
    }

    // 1. Get unbooked properties
    @GetMapping("/unbooked-properties")
    public ResponseEntity<List<Property>> getUnbookedProperties() {
        return ResponseEntity.ok(bookingService.getUnbookedProperties());
    }

    // 2. Get overlapping bookings
    @GetMapping("/overlaps")
    public ResponseEntity<List<OverlappingBookingDTO>> getOverlappingBookings() {
        return ResponseEntity.ok(bookingService.getOverlappingBookings());
    }

    // 3. Get top 5 users
    @GetMapping("/top-users")
    public ResponseEntity<List<TopUserDTO>> getTopUsersByBookings() {
        return ResponseEntity.ok(bookingService.getTopUsersByDistinctProperties());
    }
}
