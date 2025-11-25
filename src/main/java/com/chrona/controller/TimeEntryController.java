package com.chrona.controller;

import com.chrona.dto.TimeEntryDto;
import com.chrona.dto.TimeEntryRequest;
import com.chrona.service.TimeEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/time-entries")
public class TimeEntryController {

    private final TimeEntryService service;

    public TimeEntryController(TimeEntryService service) {
        this.service = service;
    }

    @PostMapping("/start/{taskId}")
    @PreAuthorize("hasAuthority('time-entry:create')")
    public ResponseEntity<TimeEntryDto> startTimer(@PathVariable Long taskId, Authentication auth) {
        return ResponseEntity.ok(service.startTimer(taskId, auth.getName()));
    }

    @PostMapping("/stop")
    @PreAuthorize("hasAuthority('time-entry:create')")
    public ResponseEntity<TimeEntryDto> stopTimer(Authentication auth) {
        return ResponseEntity.ok(service.stopTimer(auth.getName()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('time-entry:create')")
    public ResponseEntity<TimeEntryDto> createManual(@RequestBody TimeEntryRequest request, Authentication auth) {
        return ResponseEntity.ok(service.createManual(request, auth.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('time-entry:update')")
    public ResponseEntity<TimeEntryDto> update(@PathVariable Long id, @RequestBody TimeEntryRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('time-entry:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('time-entry:submit')")
    public ResponseEntity<Void> submit(@PathVariable Long id) {
        service.submit(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('time-entry:approve')")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        service.approve(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('time-entry:approve')")
    public ResponseEntity<Void> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        service.reject(id, body.get("reason"));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('time-entry:read')")
    public ResponseEntity<List<TimeEntryDto>> list(@RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(service.list(taskId, userId));
    }
}
