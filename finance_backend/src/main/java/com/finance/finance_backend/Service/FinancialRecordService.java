package com.finance.finance_backend.Service;

import com.finance.finance_backend.Dto.RecordRequest;
import com.finance.finance_backend.Dto.RecordResponse;
import com.finance.finance_backend.Exception.ResourceNotFoundException;
import com.finance.finance_backend.Model.FinancialRecord;
import com.finance.finance_backend.Model.RecordType;
import com.finance.finance_backend.Model.User;
import com.finance.finance_backend.Repository.FinancialRecordRepository;
import com.finance.finance_backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    public RecordResponse create(RecordRequest req) {
        User currentUser = getCurrentUser();
        FinancialRecord record = FinancialRecord.builder()
                .amount(req.getAmount())
                .type(req.getType())
                .category(req.getCategory())
                .date(req.getDate())
                .notes(req.getNotes())
                .createdBy(currentUser)
                .build();
        return toResponse(recordRepository.save(record));
    }
    public List<RecordResponse> getAll(String type, String category,
                                       LocalDate from, LocalDate to) {
        return recordRepository.findAllFiltered(type, category, from, to)
                .stream().map(this::toResponse).toList();
    }

    public RecordResponse getById(Long id) {
        return toResponse(findActive(id));
    }

    public RecordResponse update(Long id, RecordRequest req) {
        FinancialRecord record = findActive(id);
        record.setAmount(req.getAmount());
        record.setType(req.getType());
        record.setCategory(req.getCategory());
        record.setDate(req.getDate());
        record.setNotes(req.getNotes());
        return toResponse(recordRepository.save(record));
    }

    public void softDelete(Long id) {
        FinancialRecord record = findActive(id);
        record.setDeleted(true);
        recordRepository.save(record);
    }

    private FinancialRecord findActive(Long id) {
        FinancialRecord r = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + id));
        if (r.isDeleted()) throw new ResourceNotFoundException("Record not found: " + id);
        return r;
    }

    public RecordResponse toResponse(FinancialRecord r) {
        return RecordResponse.builder()
                .id(r.getId())
                .amount(r.getAmount())
                .type(r.getType())
                .category(r.getCategory())
                .date(r.getDate())
                .notes(r.getNotes())
                .createdBy(r.getCreatedBy() != null ? r.getCreatedBy().getName() : null)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));
    }
}