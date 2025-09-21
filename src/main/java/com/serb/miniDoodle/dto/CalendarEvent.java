package com.serb.miniDoodle.dto;

import com.serb.miniDoodle.model.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Setter
@Getter
public class CalendarEvent {
    private UUID userId;
    private List<TimeSlotDTO> timeSlots;
    private EventType type;

    public enum EventType {
        TIME_SLOT,
        MEETING
    }
}
