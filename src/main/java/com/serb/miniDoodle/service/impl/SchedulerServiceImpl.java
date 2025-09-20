package com.serb.miniDoodle.service.impl;

import com.serb.miniDoodle.model.Meeting;
import com.serb.miniDoodle.model.TimeSlot;
import com.serb.miniDoodle.model.UserCalendar;
import com.serb.miniDoodle.repository.MeetingRepository;
import com.serb.miniDoodle.repository.TimeSlotRepository;
import com.serb.miniDoodle.service.SchedulerService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    private final ConcurrentHashMap<UUID, UserCalendar> calendars = new ConcurrentHashMap<>();
    //private final Bulkhead bulkhead = Bulkhead.ofDefaults("scheduler");

    private final TimeSlotRepository timeSlotRepository;

    private final MeetingRepository meetingRepository;

    public SchedulerServiceImpl(TimeSlotRepository timeSlotRepository, MeetingRepository meetingRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.meetingRepository = meetingRepository;
    }

    @Override
    public List<UUID> listUsers() {
        return StreamSupport.stream(timeSlotRepository.findAll().spliterator(), false)
                .map(TimeSlot::getUserId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public UUID createUser() {
        return UUID.randomUUID();
    }

    @Override
    public TimeSlot createSlot(UUID userId, Instant start, Instant end, boolean busy) {
        TimeSlot newSlot = new TimeSlot(UUID.randomUUID(), userId, start, end, busy);
        return timeSlotRepository.save(newSlot);
    }

    @Override
    public boolean deleteSlot(UUID userId, UUID slotId) {
        return timeSlotRepository.findById(slotId)
                .map(slot -> {
                    if (slot.getUserId().equals(userId)) {
                        timeSlotRepository.deleteById(slotId);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    @Override
    public TimeSlot modifySlot(UUID userId, UUID slotId, Instant newStart, Instant newEnd, Boolean busy) {
        return timeSlotRepository.findById(slotId)
                .map(slot -> {
                    if (!slot.getUserId().equals(userId)) {
                        return null;
                    }
                    if (newStart != null) {
                        slot.setStart(newStart);
                    }
                    if (newEnd != null) {
                        slot.setEnd(newEnd);
                    }
                    if (busy != null) {
                        slot.setBusy(busy);
                    }
                    return timeSlotRepository.save(slot);
                }).orElse(null);
    }

    @Override
    public Meeting scheduleMeeting(UUID userId, String title, String description, List<UUID> participants, Instant start, Instant end) {
        Meeting newMeeting = new Meeting(UUID.randomUUID(), title, description, userId, participants, start, end);
        return meetingRepository.save(newMeeting);
    }

    @Override
    public boolean cancelMeeting(UUID userId, UUID meetingId) {
        return meetingRepository.findById(meetingId)
                .map(meeting -> {
                    if (meeting.getOrganizerId().equals(userId)) {
                        meetingRepository.deleteById(meetingId);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    @Override
    public List<TimeSlot> getSlots(UUID userId, Instant from, Instant to) {
        return timeSlotRepository.findByUserId(userId, from, to);
    }

    @Override
    public List<Meeting> getMeetings(UUID userId, Instant from, Instant to) {
        return meetingRepository.findByOrganizer(userId, userId, from, to);
    }
}
