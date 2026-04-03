package com.finance.finance_backend.Controller;

import com.finance.finance_backend.Dto.RecordRequest;
import com.finance.finance_backend.Dto.RecordResponse;
import com.finance.finance_backend.Service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final FinancialRecordService recordService;

    @PostMapping
    public ResponseEntity<RecordResponse> create(@Valid @RequestBody RecordRequest req) {
        return ResponseEntity.status(201).body(recordService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<RecordResponse>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(recordService.getAll(type, category, from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecordResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody RecordRequest req) {
        return ResponseEntity.ok(recordService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recordService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}