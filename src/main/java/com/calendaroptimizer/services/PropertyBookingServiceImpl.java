package com.calendaroptimizer.services;

import com.calendaroptimizer.dto.BookingRequest;
import com.calendaroptimizer.dto.OverlappingBookingDTO;
import com.calendaroptimizer.dto.TopUserDTO;
import com.calendaroptimizer.entities.Booking;
import com.calendaroptimizer.entities.Property;
import com.calendaroptimizer.entities.User;
import com.calendaroptimizer.repositories.BookingRepository;
import com.calendaroptimizer.repositories.PropertyRepository;
import com.calendaroptimizer.repositories.UserRepository;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertyBookingServiceImpl implements PropertyBookingService {
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    // 1. Get unbooked properties
    @Override
    public List<Property> getUnbookedProperties() {
        return propertyRepository.findUnbookedProperties();
    }

    // 2. Get overlapping bookings
    @Override
    public List<OverlappingBookingDTO> getOverlappingBookings() {
        return bookingRepository.findOverlappingBookings().stream()
            .map(arr -> new OverlappingBookingDTO(
                (Booking) arr[0],
                (Booking) arr[1],
                ((Booking) arr[0]).getProperty().getTitle()
            ))
            .collect(Collectors.toList());
    }

    // 3. Get top 5 users by distinct properties booked
    @Override
    public List<TopUserDTO> getTopUsersByDistinctProperties() {
        return propertyRepository.findTopUsersByDistinctPropertiesBooked().stream()
            .map(arr -> new TopUserDTO(
                (User) arr[0],
                ((Number) arr[1]).longValue()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public Booking createBooking(BookingRequest request) {
        // Check for existing overlapping bookings
        boolean hasOverlap = bookingRepository.existsByPropertyIdAndDateRange(
            request.getPropertyId(),
            request.getStartDate(),
            request.getEndDate()
        );

        if (hasOverlap) {
            throw new UnsupportedOperationException("Booking overlaps with existing reservation");
        }

        Property property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new NotFoundException("Property not found"));

        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setUser(user);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());

        return bookingRepository.save(booking);
    }
}
