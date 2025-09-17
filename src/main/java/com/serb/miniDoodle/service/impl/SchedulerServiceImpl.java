package com.serb.miniDoodle.service.impl;

import com.serb.miniDoodle.model.Meeting;
import com.serb.miniDoodle.model.TimeSlot;
import com.serb.miniDoodle.service.SchedulerService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SchedulerServiceImpl implements SchedulerService {
    @Override
    public List<UUID> listUsers() {
        return List.of();
    }

    @Override
    public UUID createUser() {
        return null;
    }

    @Override
    public TimeSlot createSlot(UUID userId, Instant start, Instant end, boolean busy) {
        return null;
    }

    @Override
    public boolean deleteSlot(UUID userId, UUID slotId) {
        return false;
    }

    @Override
    public TimeSlot modifySlot(UUID userId, UUID slotId, Instant newStart, Instant newEnd, Boolean busy) {
        return null;
    }

    @Override
    public Meeting scheduleMeeting(UUID userId, String title, String description, List<UUID> participants, Instant start, Instant end) {
        return null;
    }

    @Override
    public boolean cancelMeeting(UUID userId, UUID meetingId) {
        return false;
    }

    @Override
    public List<TimeSlot> getSlots(UUID userId, Instant from, Instant to) {
        return List.of();
    }

    @Override
    public List<Meeting> getMeetings(UUID userId, Instant from, Instant to) {
        return List.of();
    }
}
