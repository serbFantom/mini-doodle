package com.serb.miniDoodle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TimeSlotDTO {
    private UUID id;
    private Instant startTime;
    private Instant endTime;
    private boolean busy;
    private UUID userId;
}
