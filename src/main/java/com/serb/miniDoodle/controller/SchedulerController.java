package com.serb.miniDoodle.controller;

import com.serb.miniDoodle.service.SchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SchedulerController {

    private final SchedulerService svc;
    public SchedulerController(SchedulerService svc) {
        this.svc = svc;
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, UUID>> createUser() {
        UUID id = svc.createUser();
        return ResponseEntity.ok(Map.of("userId", id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UUID>> listUsers() {
        return ResponseEntity.ok(svc.listUsers());
    }
}
