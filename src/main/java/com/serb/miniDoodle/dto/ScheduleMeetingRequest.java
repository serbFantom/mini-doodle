package com.serb.miniDoodle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleMeetingRequest {
    private String title;
    private String description;
    private List<UUID> participants;
    private Instant start;
    private Instant end;
}