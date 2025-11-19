package com.chrona.controller;

import com.chrona.domain.TimesheetPeriod;
import com.chrona.dto.TimesheetPeriodActionRequest;
import com.chrona.repository.TimesheetPeriodRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/timesheet-periods")
@Validated
public class TimesheetPeriodsController {
    private final TimesheetPeriodRepository repository;

    public TimesheetPeriodsController(TimesheetPeriodRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<TimesheetPeriod> submit(@PathVariable Long id) {
        return repository.findById(id)
                .map(period -> {
                    period.setStatus("SUBMITTED");
                    period.setRejectionReason(null);
                    return ResponseEntity.ok(repository.save(period));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<TimesheetPeriod> approve(@PathVariable Long id) {
        return repository.findById(id)
                .map(period -> {
                    period.setStatus("APPROVED");
                    period.setRejectionReason(null);
                    return ResponseEntity.ok(repository.save(period));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<TimesheetPeriod> reject(@PathVariable Long id,
                                                  @Valid @RequestBody TimesheetPeriodActionRequest request) {
        return repository.findById(id)
                .map(period -> {
                    period.setStatus("REJECTED");
                    period.setRejectionReason(request.getReason());
                    return ResponseEntity.ok(repository.save(period));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
