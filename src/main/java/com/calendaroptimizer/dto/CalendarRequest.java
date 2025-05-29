package com.calendaroptimizer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarRequest {
    private WorkingHours workingHours;
    private ZoneId timeZone;
    private List<EventDto> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkingHours {
        @JsonFormat(pattern = "HH:mm")
        private LocalTime start;

        @JsonFormat(pattern = "HH:mm")
        private LocalTime end;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventDto {
        private String title;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private ZonedDateTime startTime;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private ZonedDateTime endTime;
    }
}