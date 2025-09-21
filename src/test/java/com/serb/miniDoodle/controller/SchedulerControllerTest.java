package com.serb.miniDoodle.controller;

import com.serb.miniDoodle.model.Meeting;
import com.serb.miniDoodle.model.TimeSlot;
import com.serb.miniDoodle.service.SchedulerService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SchedulerController.class)
class SchedulerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SchedulerService svc;

    private UUID userId;
    private Instant now;
    private Instant later;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        now = Instant.now();
        later = now.plusSeconds(3600);
    }


    @Test
    void testCreateSlotSuccess() throws Exception {
        when(svc.createSlot(any(), any(), any(), anyBoolean()))
                .thenReturn(new TimeSlot(UUID.randomUUID(), null, now, later, false));

        mockMvc.perform(post("/api/scheduler/{userId}/slots", userId)
                        .param("start", now.toString())
                        .param("end", later.toString())
                        .param("busy", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testScheduleMeetingSuccess() throws Exception {
        when(svc.scheduleMeeting(any(), any(), any(), any(), any(), any()))
                .thenReturn(new Meeting(UUID.randomUUID(), "Title", "Desc", null, now, later, Collections.emptyList()));

        String body = """
                {
                    "title": "Title",
                    "description": "Desc",
                    "participants": [],
                    "start": "%s",
                    "end": "%s"
                }
                """.formatted(now.toString(), later.toString());

        mockMvc.perform(post("/api/scheduler/{userId}/meetings", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void testConvertSlotsToMeetingsSuccess() throws Exception {
        when(svc.createMeetingsFromAvailableSlots(any(), any(), any(), any()))
                .thenReturn(List.of(new Meeting(UUID.randomUUID(), "Title", "Desc", null, now, later, Collections.emptyList())));

        String body = """
                {
                    "title": "Title",
                    "description": "Desc",
                    "participantIds": []
                }
                """;

        mockMvc.perform(post("/api/scheduler/{userId}/meetings/from-slots", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetCalendarSuccess() throws Exception {
        when(svc.getCalendarEvents(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/scheduler/{userId}/calendar", userId)
                        .param("from", now.toString())
                        .param("to", later.toString()))
                .andExpect(status().isOk());
    }


    @Test
    void testCreateSlotRateLimitExceeded() throws Exception {
        when(svc.createSlot(any(), any(), any(), anyBoolean()))
                .thenThrow(RequestNotPermitted.createRequestNotPermitted(RateLimiter.of("schedulerRateLimiter", RateLimiterConfig.custom().build())));

        mockMvc.perform(post("/api/scheduler/{userId}/slots", userId)
                        .param("start", now.toString())
                        .param("end", later.toString())
                        .param("busy", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void testScheduleMeetingRateLimitExceeded() throws Exception {
        when(svc.scheduleMeeting(any(), any(), any(), any(), any(), any()))
                .thenThrow(RequestNotPermitted.createRequestNotPermitted(RateLimiter.of("schedulerRateLimiter", RateLimiterConfig.custom().build())));

        String body = """
                {
                    "title": "Title",
                    "description": "Desc",
                    "participants": [],
                    "start": "%s",
                    "end": "%s"
                }
                """.formatted(now.toString(), later.toString());

        mockMvc.perform(post("/api/scheduler/{userId}/meetings", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void testConvertSlotsToMeetingsRateLimitExceeded() throws Exception {
        when(svc.createMeetingsFromAvailableSlots(any(), any(), any(), any()))
                .thenThrow(RequestNotPermitted.createRequestNotPermitted(RateLimiter.of("schedulerRateLimiter", RateLimiterConfig.custom().build())));

        String body = """
                {
                    "title": "Title",
                    "description": "Desc",
                    "participantIds": []
                }
                """;

        mockMvc.perform(post("/api/scheduler/{userId}/meetings/from-slots", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isTooManyRequests());
    }
}
