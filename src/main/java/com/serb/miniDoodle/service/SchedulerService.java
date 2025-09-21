package com.serb.miniDoodle.service;

import com.serb.miniDoodle.domain.CalendarEvent;
import com.serb.miniDoodle.model.Meeting;
import com.serb.miniDoodle.model.TimeSlot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SchedulerService {

    TimeSlot createSlot(UUID userId, Instant start, Instant end, boolean busy);

    boolean deleteSlot(UUID userId, UUID slotId);

    TimeSlot modifySlot(UUID userId, UUID slotId, Instant newStart, Instant newEnd, Boolean busy);

    Meeting scheduleMeeting(UUID userId, String title, String description, List<UUID> participants, Instant start, Instant end);

    boolean cancelMeeting(UUID userId, UUID meetingId);

    List<TimeSlot> getSlots(UUID userId, Instant from, Instant to);

    List<Meeting> getMeetings(UUID userId, Instant from, Instant to);

    List<CalendarEvent> getCalendar(UUID userId, Instant from, Instant to);

}
