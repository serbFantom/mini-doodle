package com.serb.miniDoodle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMeetingsFromSlotsRequest {
    private String title;
    private String description;
    private List<UUID> participantIds;
}
