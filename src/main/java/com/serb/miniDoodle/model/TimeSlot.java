package com.serb.miniDoodle.model;

import jakarta.persistence.*;
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
@Table(name = "time_slot")
public class TimeSlot implements Comparable<TimeSlot> {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private boolean busy;

    @Override
    public int compareTo(TimeSlot o) {
        int c = this.startTime.compareTo(o.startTime);
        if (c != 0) return c;
        c = this.endTime.compareTo(o.endTime);
        if (c != 0) return c;
        return this.id.compareTo(o.id);
    }
}
