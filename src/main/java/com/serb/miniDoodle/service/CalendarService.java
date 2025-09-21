package com.serb.miniDoodle.service;

import com.serb.miniDoodle.dto.CalendarEvent;
import com.serb.miniDoodle.model.TimeSlot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CalendarService {
    CalendarEvent getUserCalendar(UUID userId);
    List<TimeSlot> getUserFreeSlots(UUID userId, Instant start, Instant end);
    List<TimeSlot> getUserBusySlots(UUID userId, Instant start, Instant end);
}
