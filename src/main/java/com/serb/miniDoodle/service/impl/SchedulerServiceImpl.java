package com.serb.miniDoodle.service.impl;

import com.serb.miniDoodle.domain.CalendarEvent;
import com.serb.miniDoodle.model.User;
import com.serb.miniDoodle.repository.UserRepository;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.decorators.Decorators;
import com.serb.miniDoodle.model.Meeting;
import com.serb.miniDoodle.model.TimeSlot;
import com.serb.miniDoodle.repository.MeetingRepository;
import com.serb.miniDoodle.repository.TimeSlotRepository;
import com.serb.miniDoodle.service.SchedulerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    private final Bulkhead bulkhead = Bulkhead.ofDefaults("scheduler");

    private final TimeSlotRepository timeSlotRepository;

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    public SchedulerServiceImpl(TimeSlotRepository timeSlotRepository, MeetingRepository meetingRepository, UserRepository userRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
    }

    private <T> T withBulkhead(Supplier<T> supplier) {
        return Decorators.ofSupplier(supplier).withBulkhead(bulkhead).get();
    }

    @Override
    public TimeSlot createSlot(UUID userId, Instant startTime, Instant endTime, boolean busy) {
        // Fetch the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return withBulkhead(() -> {
            TimeSlot newSlot = new TimeSlot(UUID.randomUUID(), user, startTime, endTime, busy);
            return timeSlotRepository.save(newSlot);
        });
    }

    @Override
    public boolean deleteSlot(UUID userId, UUID slotId) {
        return timeSlotRepository.findById(slotId)
                .map(slot -> {
                    if (slot.getUser().getId().equals(userId)) {
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
                    if (!slot.getUser().getId().equals(userId)) {
                        return null;
                    }
                    if (newStart != null) {
                        slot.setStartTime(newStart);
                    }
                    if (newEnd != null) {
                        slot.setEndTime(newEnd);
                    }
                    if (busy != null) {
                        slot.setBusy(busy);
                    }
                    return timeSlotRepository.save(slot);
                }).orElse(null);
    }

    @Override
    public Meeting scheduleMeeting(UUID userId, String title, String description, List<UUID> participants, Instant start, Instant end) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        Meeting newMeeting = new Meeting(UUID.randomUUID(), title, description, user, null, start, end, null);
        return meetingRepository.save(newMeeting);
    }

    @Override
    public boolean cancelMeeting(UUID userId, UUID meetingId) {
        return meetingRepository.findById(meetingId)
                .map(meeting -> {
                    if (meeting.getOrganizer().getId().equals(userId)) {
                        meetingRepository.deleteById(meetingId);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    @Override
    public List<TimeSlot> getSlots(UUID userId, Instant from, Instant to) {
        return timeSlotRepository.findByUserIdStartEndTime(userId, from, to);
    }

    @Override
    public List<Meeting> getMeetings(UUID userId, Instant from, Instant to) {

        return meetingRepository.findByOrganizerIdOrParticipants(userId, userId, from, to);
    }

    @Override
    public List<CalendarEvent> getCalendar(UUID userId, Instant from, Instant to) {
        List<CalendarEvent> timeSlots = getSlots(userId, from, to).stream()
                .map(slot -> new CalendarEvent(slot.getId(), List.of(), CalendarEvent.EventType.TIME_SLOT))
                .toList();

        List<CalendarEvent> meetings = getMeetings(userId, from, to).stream()
                .map(meeting -> new CalendarEvent(meeting.getId(), List.of(), CalendarEvent.EventType.MEETING))
                .toList();

        return Stream.concat(timeSlots.stream(), meetings.stream())
                //.sorted(Comparator.comparing(CalendarEvent::getStart))
                .collect(Collectors.toList());
    }
}
