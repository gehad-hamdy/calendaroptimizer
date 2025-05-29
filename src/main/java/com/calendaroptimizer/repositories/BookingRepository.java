package com.calendaroptimizer.repositories;

import com.calendaroptimizer.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 2. Detect overlapping bookings
    @Query("SELECT b1, b2 FROM Booking b1 JOIN Booking b2 " +
        "ON b1.property = b2.property AND b1.id < b2.id " +
        "WHERE b1.endDate > b2.startDate AND b2.endDate > b1.startDate")
    List<Object[]> findOverlappingBookings();

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
        "FROM Booking b " +
        "WHERE b.property.id = :propertyId " +
        "AND b.endDate > :startDate " +
        "AND b.startDate < :endDate")
    boolean existsByPropertyIdAndDateRange(
        @Param("propertyId") Long propertyId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
