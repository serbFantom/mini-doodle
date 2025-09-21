package com.serb.miniDoodle.service.impl;

import com.serb.miniDoodle.model.TimeSlot;
import com.serb.miniDoodle.model.User;
import com.serb.miniDoodle.repository.MeetingRepository;
import com.serb.miniDoodle.repository.TimeSlotRepository;
import com.serb.miniDoodle.repository.UserRepository;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

class SchedulerServiceImplTest {

    private SchedulerServiceImpl service;
    private TimeSlotRepository timeSlotRepository;
    private MeetingRepository meetingRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        timeSlotRepository = mock(TimeSlotRepository.class);
        meetingRepository = mock(MeetingRepository.class);
        userRepository = mock(UserRepository.class);
        service = new SchedulerServiceImpl(timeSlotRepository, meetingRepository, userRepository);
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(timeSlotRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(meetingRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    }

    private <T> int runConcurrentTasks(int taskCount, Callable<T> task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(taskCount);
        List<Future<T>> futures = new ArrayList<>();
        IntStream.range(0, taskCount).forEach(i -> futures.add(executor.submit(task)));

        int failedCount = 0;
        for (Future<T> f : futures) {
            try {
                f.get();
            } catch (ExecutionException ex) {
                if (ex.getCause() instanceof BulkheadFullException) {
                    failedCount++;
                }
            }
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        return failedCount;
    }

    @Test
    void testCreateSlotBulkhead() throws InterruptedException {
        UUID userId = UUID.randomUUID();
        Instant start = Instant.now();
        Instant end = start.plusSeconds(3600);

        int taskCount = 1000;
        int failed = runConcurrentTasks(taskCount, () -> service.createSlot(userId, start, end, false));

        System.out.println("createSlot failed count: " + failed);
        assertTrue(failed > 0, "Some calls should fail due to Bulkhead limit");
    }

    @Test
    void testScheduleMeetingBulkhead() throws InterruptedException {
        UUID userId = UUID.randomUUID();
        Instant start = Instant.now();
        Instant end = start.plusSeconds(3600);
        List<UUID> participants = Collections.emptyList();
        int taskCount = 100;
        int failed = runConcurrentTasks(taskCount,
                () -> service.scheduleMeeting(userId, "Meeting", "Desc", participants, start, end));

        System.out.println("scheduleMeeting failed count: " + failed);
        assertTrue(failed > 0, "Some calls should fail due to Bulkhead limit");
    }

    @Test
    void testCreateMeetingsFromAvailableSlotsBulkhead() throws InterruptedException {
        UUID userId = UUID.randomUUID();
        List<UUID> participantIds = Collections.emptyList();

        // Mock timeSlotRepository
        when(timeSlotRepository.findByUserIdAndBusy(userId, false))
                .thenReturn(List.of(new TimeSlot(UUID.randomUUID(), new User(), Instant.now(), Instant.now().plusSeconds(3600), false)));

        int taskCount = 100;
        int failed = runConcurrentTasks(taskCount,
                () -> service.createMeetingsFromAvailableSlots(userId, "Title", "Desc", participantIds));

        System.out.println("createMeetingsFromAvailableSlots failed count: " + failed);
        assertTrue(failed > 0, "Some calls should fail due to Bulkhead limit");
    }
}
