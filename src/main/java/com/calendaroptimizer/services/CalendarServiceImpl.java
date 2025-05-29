package com.calendaroptimizer.services;

import com.calendaroptimizer.dto.CalendarRequest;
import com.calendaroptimizer.dto.ConflictResponse;
import com.calendaroptimizer.dto.ConflictResponse.TimeSlot;
import com.calendaroptimizer.dto.Event;
import com.calendaroptimizer.repositories.BookingRepository;
import com.calendaroptimizer.repositories.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CalendarServiceImpl implements CalendarService {
    public ConflictResponse analyzeCalendar(CalendarRequest request) {
        // Parse and sort events
        List<Event> events = request.getEvents().stream()
            .map(dto -> parseEvent(dto, request.getTimeZone()))
            .sorted(Comparator.comparing(Event::getStart))
            .collect(Collectors.toList());

        // Find conflicts
        List<ConflictResponse.Conflict> conflicts = findConflicts(events);

        // Find free slots within working hours
        List<ConflictResponse.TimeSlot> freeSlots = findFreeSlots(
            events,
            request.getWorkingHours(),
            request.getTimeZone()
        );

        return new ConflictResponse(conflicts, freeSlots);
    }

    private Event parseEvent(CalendarRequest.EventDto eventDto, ZoneId timeZone) {
        return new Event(
            eventDto.getTitle(),
            eventDto.getStartTime().withZoneSameInstant(timeZone),
            eventDto.getEndTime().withZoneSameInstant(timeZone)
        );
    }

    private List<ConflictResponse.Conflict> findConflicts(List<Event> events) {
        List<ConflictResponse.Conflict> conflicts = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            Event event1 = events.get(i);

            for (int j = i + 1; j < events.size(); j++) {
                Event event2 = events.get(j);

                // If event1 ends before event2 starts, no need to check further
                if (!event1.getEnd().isAfter(event2.getStart())) {
                    break;
                }

                // Calculate overlap
                ZonedDateTime overlapStart = later(event1.getStart(), event2.getStart());
                ZonedDateTime overlapEnd = earlier(event1.getEnd(), event2.getEnd());

                if (overlapStart.isBefore(overlapEnd)) {
                    conflicts.add(new ConflictResponse.Conflict(
                        event1.getTitle(),
                        event2.getTitle(),
                        overlapStart,
                        overlapEnd
                    ));
                }
            }
        }

        return conflicts;
    }

    private List<TimeSlot> findFreeSlots(List<Event> events,
        CalendarRequest.WorkingHours workingHours,
        ZoneId timeZone) {
        List<TimeSlot> freeSlots = new ArrayList<>();

        ZonedDateTime workStart = getWorkStart(workingHours, timeZone, events);
        ZonedDateTime workEnd = getWorkEnd(workingHours, timeZone, events);

        if (events.isEmpty()) {
            freeSlots.add(new TimeSlot(workStart, workEnd));
            return freeSlots;
        }

        addFreeSlotBeforeFirstEvent(events, freeSlots, workStart);
        addFreeSlotsBetweenEvents(events, freeSlots);
        addFreeSlotAfterLastEvent(events, freeSlots, workEnd);

        return filterAndMergeFreeSlots(freeSlots, workStart, workEnd);
    }

    private ZonedDateTime getWorkStart(CalendarRequest.WorkingHours workingHours, ZoneId timeZone, List<Event> events) {
        LocalDate currentDate = events.isEmpty() ? LocalDate.now() : events.get(0).getStart().toLocalDate();
        return ZonedDateTime.of(currentDate, workingHours.getStart(), timeZone);
    }

    private ZonedDateTime getWorkEnd(CalendarRequest.WorkingHours workingHours, ZoneId timeZone, List<Event> events) {
        LocalDate currentDate = events.isEmpty() ? LocalDate.now() : events.get(0).getStart().toLocalDate();
        return ZonedDateTime.of(currentDate, workingHours.getEnd(), timeZone);
    }

    private void addFreeSlotBeforeFirstEvent(List<Event> events, List<TimeSlot> freeSlots, ZonedDateTime workStart) {
        ZonedDateTime firstEventStart = events.get(0).getStart();
        if (workStart.isBefore(firstEventStart)) {
            freeSlots.add(new TimeSlot(workStart, firstEventStart));
        }
    }

    private void addFreeSlotsBetweenEvents(List<Event> events, List<TimeSlot> freeSlots) {
        ZonedDateTime lastEventEnd = events.get(0).getEnd();

        for (int i = 1; i < events.size(); i++) {
            Event current = events.get(i);

            if (lastEventEnd.isBefore(current.getStart())) {
                freeSlots.add(new TimeSlot(lastEventEnd, current.getStart()));
            }

            if (current.getEnd().isAfter(lastEventEnd)) {
                lastEventEnd = current.getEnd();
            }
        }
    }

    private void addFreeSlotAfterLastEvent(List<Event> events, List<TimeSlot> freeSlots, ZonedDateTime workEnd) {
        ZonedDateTime lastEventEnd = events.get(events.size() - 1).getEnd();
        if (lastEventEnd.isBefore(workEnd)) {
            freeSlots.add(new TimeSlot(lastEventEnd, workEnd));
        }
    }

    private List<TimeSlot> filterAndMergeFreeSlots(List<TimeSlot> freeSlots, ZonedDateTime workStart, ZonedDateTime workEnd) {
        return mergeAdjacentSlots(
            freeSlots.stream()
                .filter(slot -> slot.getStart().isBefore(slot.getEnd()))
                .filter(slot -> isWithinWorkingHours(slot, workStart, workEnd))
                .collect(Collectors.toList())
        );
    }

    private boolean isWithinWorkingHours(TimeSlot slot, ZonedDateTime workStart, ZonedDateTime workEnd) {
        boolean startsAfterWorkStart = !slot.getStart().isBefore(workStart);
        boolean endsBeforeWorkEnd = !slot.getEnd().isAfter(workEnd);
        return startsAfterWorkStart && endsBeforeWorkEnd;
    }

    private List<ConflictResponse.TimeSlot> mergeAdjacentSlots(List<ConflictResponse.TimeSlot> slots) {
        if (slots.size() <= 1) {
            return slots;
        }

        List<ConflictResponse.TimeSlot> merged = new ArrayList<>();
        ConflictResponse.TimeSlot current = slots.get(0);

        for (int i = 1; i < slots.size(); i++) {
            ConflictResponse.TimeSlot next = slots.get(i);

            if (current.getEnd().equals(next.getStart())) {
                // Merge adjacent slots
                current = new ConflictResponse.TimeSlot(current.getStart(), next.getEnd());
            } else {
                merged.add(current);
                current = next;
            }
        }

        merged.add(current);
        return merged;
    }

    private ZonedDateTime earlier(ZonedDateTime dt1, ZonedDateTime dt2) {
        return dt1.isBefore(dt2) ? dt1 : dt2;
    }

    private ZonedDateTime later(ZonedDateTime dt1, ZonedDateTime dt2) {
        return dt1.isAfter(dt2) ? dt1 : dt2;
    }

}