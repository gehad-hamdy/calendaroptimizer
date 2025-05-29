package com.calendaroptimizer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class Event {
    private String title;
    private ZonedDateTime start;
    private ZonedDateTime end;
}