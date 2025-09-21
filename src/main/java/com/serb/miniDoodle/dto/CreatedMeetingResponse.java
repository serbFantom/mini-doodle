package com.serb.miniDoodle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedMeetingResponse {
    private UUID meetingId;
    private String title;
    private Instant start;
    private Instant end;
}
