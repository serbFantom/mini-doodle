package com.serb.miniDoodle.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Meeting {
    private final UUID id;
    private final String title;
    private final UUID organizerId;
    private final Instant start;
    private final Instant end;
}
