package com.serb.miniDoodle.controller;

import com.serb.miniDoodle.dto.CalendarEvent;
import com.serb.miniDoodle.dto.CreateMeetingsFromSlotsRequest;
import com.serb.miniDoodle.dto.CreatedMeetingResponse;
import com.serb.miniDoodle.dto.ScheduleMeetingRequest;
import com.serb.miniDoodle.model.Meeting;
import com.serb.miniDoodle.service.SchedulerService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
@RateLimiter(name = "schedulerRateLimiter")
public class SchedulerController {
    private final SchedulerService svc;

    // --- Slot Management ---
    @PostMapping("/{userId}/slots")
    public ResponseEntity<?> createSlot(@PathVariable UUID userId, @RequestParam Instant start,
                                        @RequestParam Instant end, @RequestParam(defaultValue = "false") boolean busy) {
            return ResponseEntity.ok(svc.createSlot(userId, start, end, busy));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}/slots/{slotId}")
    public ResponseEntity<?> deleteSlot(@PathVariable UUID userId, @PathVariable UUID slotId) {
            return ResponseEntity.ok(Map.of("deleted", svc.deleteSlot(userId, slotId)));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{userId}/slots/{slotId}")
    public ResponseEntity<?> modifySlot(@PathVariable UUID userId, @PathVariable UUID slotId,
                                        @RequestParam(required = false) Instant start,
                                        @RequestParam(required = false) Instant end,
                                        @RequestParam(required = false) Boolean busy) {
            return ResponseEntity.ok(svc.modifySlot(userId, slotId, start, end, busy));
    }

    @PostMapping("/{userId}/meetings")
    public ResponseEntity<Meeting> scheduleMeeting(
            @PathVariable UUID userId,
            @RequestBody ScheduleMeetingRequest request) {

        Meeting meeting = svc.scheduleMeeting(
                userId,
                request.getTitle(),
                request.getDescription(),
                request.getParticipants(),
                request.getStart(),
                request.getEnd()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(meeting);
    }

    @PostMapping("/{userId}/meetings/from-slots")
    public ResponseEntity<List<CreatedMeetingResponse>> convertSlotsToMeetings(
            @PathVariable UUID userId,
            @RequestBody CreateMeetingsFromSlotsRequest request) {

        List<Meeting> meetings = svc.createMeetingsFromAvailableSlots(
                userId,
                request.getTitle(),
                request.getDescription(),
                request.getParticipantIds()
        );
        List<CreatedMeetingResponse> response = meetings.stream()
                .map(m -> new CreatedMeetingResponse(m.getId(), m.getTitle(), m.getStartTime(), m.getEndTime()))
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}/calendar")
    public ResponseEntity<List<CalendarEvent>> getCalendar(
            @PathVariable UUID userId,
            @RequestParam("from") Instant from,
            @RequestParam("to") Instant to) {

        List<CalendarEvent> events = svc.getCalendarEvents(userId, from, to);
        return ResponseEntity.ok(events);
    }


}
