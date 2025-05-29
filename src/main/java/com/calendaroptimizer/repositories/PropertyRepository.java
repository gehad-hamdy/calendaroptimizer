package com.calendaroptimizer.repositories;

import com.calendaroptimizer.entities.Property;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // 1. Find unbooked properties
    @Query("SELECT p FROM Property p WHERE p.id NOT IN (SELECT DISTINCT b.property.id FROM Booking b)")
    List<Property> findUnbookedProperties();

    // 3. Top 5 users by distinct properties booked
    @Query("SELECT u, COUNT(DISTINCT b.property) as propertyCount " +
        "FROM User u JOIN Booking b ON u.id = b.user.id " +
        "GROUP BY u.id ORDER BY propertyCount DESC LIMIT 5")
    List<Object[]> findTopUsersByDistinctPropertiesBooked();
}
