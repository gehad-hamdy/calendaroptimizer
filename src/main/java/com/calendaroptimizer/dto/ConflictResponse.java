package com.calendaroptimizer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConflictResponse {
    private List<Conflict> conflicts;
    private List<TimeSlot> freeSlots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Conflict {
        private String event1;
        private String event2;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private ZonedDateTime overlapStart;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private ZonedDateTime overlapEnd;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlot {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private ZonedDateTime start;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private ZonedDateTime end;
    }
}