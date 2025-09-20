package com.serb.miniDoodle.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class TimeSlot implements Comparable<TimeSlot> {
    @Id
    private UUID id;
    private UUID userId;
    private Instant start;
    private Instant end;
    private boolean busy;

    @Override
    public int compareTo(TimeSlot o) {
        int c = this.start.compareTo(o.start);
        if (c != 0) return c;
        c = this.end.compareTo(o.end);
        if (c != 0) return c;
        return this.id.compareTo(o.id);
    }
}
