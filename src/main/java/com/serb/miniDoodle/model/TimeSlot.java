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
public class TimeSlot implements Comparable<TimeSlot>{
    private final UUID id;
    private final Instant start;
    private final Instant end;

    @Override
    public int compareTo(TimeSlot o) {
        int c = this.start.compareTo(o.start);
        if (c != 0) return c;
        c = this.end.compareTo(o.end);
        if (c != 0) return c;
        return this.id.compareTo(o.id);
    }
}
