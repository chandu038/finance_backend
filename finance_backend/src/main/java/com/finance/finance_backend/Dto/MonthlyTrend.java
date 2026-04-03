package com.finance.finance_backend.Dto;

import com.finance.finance_backend.Model.RecordType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MonthlyTrend {
    private String month;
    private RecordType type;
    private BigDecimal amount;
}

