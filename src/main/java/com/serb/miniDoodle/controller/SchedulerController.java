package com.serb.miniDoodle.controller;

import com.serb.miniDoodle.domain.CalendarEvent;
import com.serb.miniDoodle.service.SchedulerService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class SchedulerController {

    private final SchedulerService svc;

    public SchedulerController(SchedulerService svc) {
        this.svc = svc;
    }


    // --- Slot Management ---
    @PostMapping("/users/{userId}/slots")
    @RateLimiter(name = "createSlotRateLimiter", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<?> createSlot(@PathVariable UUID userId, @RequestParam Instant start,
                                        @RequestParam Instant end, @RequestParam(defaultValue = "false") boolean busy) {
        try {
            return ResponseEntity.ok(svc.createSlot(userId, start, end, busy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}/slots/{slotId}")
    public ResponseEntity<?> deleteSlot(@PathVariable UUID userId, @PathVariable UUID slotId) {
        try {
            return ResponseEntity.ok(Map.of("deleted", svc.deleteSlot(userId, slotId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/users/{userId}/slots/{slotId}")
    public ResponseEntity<?> modifySlot(@PathVariable UUID userId, @PathVariable UUID slotId,
                                        @RequestParam(required = false) Instant start,
                                        @RequestParam(required = false) Instant end,
                                        @RequestParam(required = false) Boolean busy) {
        try {
            return ResponseEntity.ok(svc.modifySlot(userId, slotId, start, end, busy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/{userId}/calendar")
    public ResponseEntity<List<CalendarEvent>> getCalendar(@PathVariable UUID userId, @RequestParam Instant from, @RequestParam Instant to) {
        return ResponseEntity.ok(svc.getCalendar(userId, from, to));
    }

    public ResponseEntity<?> rateLimitFallback(UUID userId, Instant start, Instant end, boolean busy, Throwable t) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS.value())  // HTTP 429 Too Many Requests
                .body(Map.of("error", "Too many requests. Please try again later."));
    }


}
