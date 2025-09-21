package com.serb.miniDoodle.domain;

import com.serb.miniDoodle.model.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Setter
@Getter
public class CalendarEvent {
    private UUID userId;
    private List<TimeSlot> timeSlots;
    private EventType type;

    public enum EventType {
        TIME_SLOT,
        MEETING
    }
}
