package com.serb.miniDoodle.service.impl;

import com.serb.miniDoodle.dto.CalendarEvent;
import com.serb.miniDoodle.model.TimeSlot;
import com.serb.miniDoodle.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    @Override
    public CalendarEvent getUserCalendar(UUID userId) {
        return null;
    }

    @Override
    public List<TimeSlot> getUserFreeSlots(UUID userId, Instant start, Instant end) {
        return List.of();
    }

    @Override
    public List<TimeSlot> getUserBusySlots(UUID userId, Instant start, Instant end) {
        return List.of();
    }
}
