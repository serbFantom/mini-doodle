package com.serb.miniDoodle.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class UserCalendar {
    private final UUID userId;
    private final NavigableSet<TimeSlot> available = new TreeSet<>();
    private final NavigableSet<Meeting> meetings = new TreeSet<>(Comparator.comparing(Meeting::getStart));
}
