package com.calendaroptimizer.services;

import com.calendaroptimizer.dto.CalendarRequest;
import com.calendaroptimizer.dto.ConflictResponse;

public interface CalendarService {
    ConflictResponse analyzeCalendar(CalendarRequest request);
}
