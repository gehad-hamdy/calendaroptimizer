package com.calendaroptimizer.services;

import com.calendaroptimizer.dto.BookingRequest;
import com.calendaroptimizer.dto.OverlappingBookingDTO;
import com.calendaroptimizer.dto.TopUserDTO;
import com.calendaroptimizer.entities.Booking;
import com.calendaroptimizer.entities.Property;
import java.util.List;

public interface PropertyBookingService {
    List<Property> getUnbookedProperties();

    List<OverlappingBookingDTO> getOverlappingBookings();

    List<TopUserDTO> getTopUsersByDistinctProperties();

    Booking createBooking(BookingRequest request);
}
