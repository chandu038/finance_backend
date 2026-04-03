package com.finance.finance_backend.Service;

import com.finance.finance_backend.Dto.DashboardResponse;
import com.finance.finance_backend.Dto.MonthlyTrend;
import com.finance.finance_backend.Dto.RecordResponse;
import com.finance.finance_backend.Model.RecordType;
import com.finance.finance_backend.Repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;
    private final FinancialRecordService recordService;

    public DashboardResponse getSummary() {
        BigDecimal totalIncome  = recordRepository.totalIncome();
        BigDecimal totalExpense = recordRepository.totalExpense();
        BigDecimal netBalance   = totalIncome.subtract(totalExpense);
        Map<String, BigDecimal> categoryMap = new LinkedHashMap<>();
        for (Object[] row : recordRepository.categoryTotals()) {
            categoryMap.put((String) row[0], (BigDecimal) row[1]);
        }

        List<MonthlyTrend> trends = recordRepository.monthlyTotals().stream()
                .map(row -> {
                    String month = (String) row[0];
                    RecordType type = (RecordType) row[1];  // cast directly, no valueOf()
                    BigDecimal amount = (BigDecimal) row[2];
                    return new MonthlyTrend(month, type, amount);
                })
                .toList();

        // Recent 10 records
        List<RecordResponse> recent = recordRepository.recentActivity()
                .stream()
                .map(recordService::toResponse)
                .toList();

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .categoryTotals(categoryMap)
                .monthlyTrends(trends)
                .recentActivity(recent)
                .build();
    }
}