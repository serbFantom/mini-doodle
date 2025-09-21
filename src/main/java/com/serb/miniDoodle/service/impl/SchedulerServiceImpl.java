package com.serb.miniDoodle.service.impl;

import com.serb.miniDoodle.dto.CalendarEvent;
import com.serb.miniDoodle.dto.TimeSlotDTO;
import com.serb.miniDoodle.model.User;
import com.serb.miniDoodle.repository.UserRepository;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.decorators.Decorators;
import com.serb.miniDoodle.model.Meeting;
import com.serb.miniDoodle.model.TimeSlot;
import com.serb.miniDoodle.repository.MeetingRepository;
import com.serb.miniDoodle.repository.TimeSlotRepository;
import com.serb.miniDoodle.service.SchedulerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulerServiceImpl implements SchedulerService {

    private final Bulkhead bulkhead = Bulkhead.ofDefaults("scheduler");

    private final TimeSlotRepository timeSlotRepository;

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    private <T> T withBulkhead(Supplier<T> supplier) {
        return Decorators.ofSupplier(supplier).withBulkhead(bulkhead).get();
    }

    // --- Bulkheads ---
    private final Bulkhead createSlotBulkhead = Bulkhead.of("createSlotBulkhead",
            BulkheadConfig.custom()
                    .maxConcurrentCalls(10)
                    .maxWaitDuration(Duration.ofMillis(0))
                    .build());

    private final Bulkhead scheduleMeetingBulkhead = Bulkhead.of("scheduleMeetingBulkhead",
            BulkheadConfig.custom()
                    .maxConcurrentCalls(5)
                    .maxWaitDuration(Duration.ofMillis(0))
                    .build());

    private final Bulkhead createMeetingsBulkhead = Bulkhead.of("createMeetingsBulkhead",
            BulkheadConfig.custom()
                    .maxConcurrentCalls(3)
                    .maxWaitDuration(Duration.ofMillis(0))
                    .build());

    // --- Helper for Bulkhead wrapping ---
    private <T> T withBulkhead(Bulkhead bulkhead, Supplier<T> supplier, Supplier<T> fallback) {
        return Decorators.ofSupplier(supplier)
                .withBulkhead(bulkhead)
                .get();
    }

    // --- Slot Management ---

    @Override
    public TimeSlot createSlot(UUID userId, Instant startTime, Instant endTime, boolean busy) {
        return withBulkhead(
                createSlotBulkhead,
                () -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
                    TimeSlot newSlot = new TimeSlot(UUID.randomUUID(), user, startTime, endTime, busy);
                    return timeSlotRepository.save(newSlot);
                },
                () -> {
                    throw new RuntimeException("Too many concurrent createSlot requests. Try again later.");
                }
        );
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
                    if (newStart != null) slot.setStartTime(newStart);
                    if (newEnd != null) slot.setEndTime(newEnd);
                    if (busy != null) slot.setBusy(busy);
                    return timeSlotRepository.save(slot);
                }).orElse(null);
    }

    // --- Meeting Management ---

    @Override
    public Meeting scheduleMeeting(UUID userId, String title, String description,
                                   List<UUID> participants, Instant start, Instant end) {
        return withBulkhead(
                scheduleMeetingBulkhead,
                () -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

                    Meeting newMeeting = new Meeting(UUID.randomUUID(), title, description, user, start, end, null);

                    if (participants != null && !participants.isEmpty()) {
                        List<User> participantUsers = userRepository.findAllById(participants);
                        if (participantUsers.size() != participants.size()) {
                            throw new EntityNotFoundException("One or more participants not found");
                        }
                        newMeeting.setParticipants(participantUsers);
                    }

                    return meetingRepository.saveAndFlush(newMeeting);
                },
                () -> {
                    throw new RuntimeException("Too many concurrent scheduleMeeting requests. Try again later.");
                }
        );
    }

    @Override
    public boolean cancelMeeting(UUID userId, UUID meetingId) {
        return meetingRepository.findById(meetingId)
                .map(meeting -> {
                    if (meeting.getUser().getId().equals(userId)) {
                        meetingRepository.deleteById(meetingId);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    // --- Retrieve Slots and Meetings ---

    @Override
    public List<TimeSlot> getSlots(UUID userId, Instant from, Instant to) {
        return timeSlotRepository.findByUserIdAndStartTimeBetween(userId, from, to);
    }

    @Override
    public List<Meeting> getMeetings(UUID userId, Instant from, Instant to) {
        return meetingRepository.findByUserIdAndStartTimeBetween(userId, from, to);
    }

    @Override
    public List<Meeting> createMeetingsFromAvailableSlots(UUID userId, String title, String description, List<UUID> participantIds) {
        return withBulkhead(
                createMeetingsBulkhead,
                () -> {
                    User organizer = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found"));

                    List<TimeSlot> availableSlots = timeSlotRepository.findByUserIdAndBusy(userId, false);

                    List<User> participants = participantIds != null && !participantIds.isEmpty()
                            ? userRepository.findAllById(participantIds)
                            : Collections.emptyList();

                    List<Meeting> meetings = new ArrayList<>();

                    for (TimeSlot slot : availableSlots) {
                        Meeting meeting = new Meeting(
                                UUID.randomUUID(),
                                title,
                                description,
                                organizer,
                                slot.getStartTime(),
                                slot.getEndTime(),
                                new ArrayList<>(participants)
                        );
                        meetings.add(meetingRepository.save(meeting));
                    }

                    return meetings;
                },
                () -> {
                    throw new RuntimeException("Too many concurrent createMeetingsFromAvailableSlots requests. Try again later.");
                }
        );
    }

    // --- Calendar Events ---

    @Override
    public List<CalendarEvent> getCalendarEvents(UUID userId, Instant from, Instant to) {
        List<TimeSlotDTO> slotDTOs = timeSlotRepository.findByUserIdAndStartTimeBetween(userId, from, to)
                .stream()
                .map(slot -> new TimeSlotDTO(slot.getId(), slot.getStartTime(), slot.getEndTime(), slot.isBusy(), slot.getUser().getId()))
                .toList();

        List<TimeSlotDTO> meetingDTOs = meetingRepository.findByUserIdAndStartTimeBetween(userId, from, to)
                .stream()
                .map(meeting -> new TimeSlotDTO(meeting.getId(), meeting.getStartTime(), meeting.getEndTime(), true, meeting.getUser().getId()))
                .toList();

        List<CalendarEvent> events = new ArrayList<>();
        if (!slotDTOs.isEmpty()) events.add(new CalendarEvent(userId, slotDTOs, CalendarEvent.EventType.TIME_SLOT));
        if (!meetingDTOs.isEmpty()) events.add(new CalendarEvent(userId, meetingDTOs, CalendarEvent.EventType.MEETING));

        return events;
    }
}
