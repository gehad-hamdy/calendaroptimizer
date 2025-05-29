package com.calendaroptimizer;

import com.calendaroptimizer.dto.CalendarRequest;
import com.calendaroptimizer.dto.ConflictResponse;
import com.calendaroptimizer.services.CalendarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CalendarServiceTest {

    @Autowired
    private CalendarService calendarService;

    @Test
    void testAnalyzeCalendar() {
        // Given
        CalendarRequest request = new CalendarRequest();
        request.setWorkingHours(new CalendarRequest.WorkingHours(LocalTime.of(9, 0), LocalTime.of(17, 0)));
        request.setTimeZone(ZoneId.of("Asia/Riyadh"));

        request.setEvents(List.of(
            new CalendarRequest.EventDto(
                "Team Standup",
                ZonedDateTime.parse("2025-05-14T09:30:00+03:00"),
                ZonedDateTime.parse("2025-05-14T10:00:00+03:00")),
            new CalendarRequest.EventDto(
                "Client Meeting",
                ZonedDateTime.parse("2025-05-14T10:00:00+03:00"),
                ZonedDateTime.parse("2025-05-14T11:00:00+03:00")),
            new CalendarRequest.EventDto(
                "1:1 Review",
                ZonedDateTime.parse("2025-05-14T10:30:00+03:00"),
                ZonedDateTime.parse("2025-05-14T11:30:00+03:00")),
            new CalendarRequest.EventDto(
                "Lunch Break",
                ZonedDateTime.parse("2025-05-14T13:00:00+03:00"),
                ZonedDateTime.parse("2025-05-14T14:00:00+03:00"))
        ));

        // When
        ConflictResponse response = calendarService.analyzeCalendar(request);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getConflicts().size());

        ConflictResponse.Conflict conflict = response.getConflicts().get(0);
        assertEquals("Client Meeting", conflict.getEvent1());
        assertEquals("1:1 Review", conflict.getEvent2());
        assertEquals(ZonedDateTime.parse("2025-05-14T10:30:00+03:00"), conflict.getOverlapStart());
        assertEquals(ZonedDateTime.parse("2025-05-14T11:00:00+03:00"), conflict.getOverlapEnd());

        assertEquals(3, response.getFreeSlots().size());

        List<ConflictResponse.TimeSlot> freeSlots = response.getFreeSlots();
        assertEquals(ZonedDateTime.parse("2025-05-14T09:00:00+03:00"), freeSlots.get(0).getStart());
        assertEquals(ZonedDateTime.parse("2025-05-14T09:30:00+03:00"), freeSlots.get(0).getEnd());

        assertEquals(ZonedDateTime.parse("2025-05-14T11:30:00+03:00"), freeSlots.get(1).getStart());
        assertEquals(ZonedDateTime.parse("2025-05-14T13:00:00+03:00"), freeSlots.get(1).getEnd());

        assertEquals(ZonedDateTime.parse("2025-05-14T14:00:00+03:00"), freeSlots.get(2).getStart());
        assertEquals(ZonedDateTime.parse("2025-05-14T17:00:00+03:00"), freeSlots.get(2).getEnd());
    }
}