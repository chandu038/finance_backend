package com.finance.finance_backend.Repository;

import com.finance.finance_backend.Model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    @Query(value = """
        SELECT * FROM financial_records
        WHERE deleted = false
          AND (:type IS NULL OR type = CAST(:type AS VARCHAR))
          AND (:category IS NULL OR category ILIKE :category)
          AND (:from IS NULL OR date >= CAST(:from AS DATE))
          AND (:to IS NULL OR date <= CAST(:to AS DATE))
        ORDER BY date DESC
    """, nativeQuery = true)
    List<FinancialRecord> findAllFiltered(
            @Param("type") String type,
            @Param("category") String category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.deleted = false AND r.type = 'INCOME'")
    BigDecimal totalIncome();

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.deleted = false AND r.type = 'EXPENSE'")
    BigDecimal totalExpense();

    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.deleted = false GROUP BY r.category")
    List<Object[]> categoryTotals();

    @Query("""
        SELECT FUNCTION('TO_CHAR', r.date, 'YYYY-MM'), r.type, SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.deleted = false
        GROUP BY FUNCTION('TO_CHAR', r.date, 'YYYY-MM'), r.type
        ORDER BY 1
    """)
    List<Object[]> monthlyTotals();

    @Query("SELECT r FROM FinancialRecord r WHERE r.deleted = false ORDER BY r.createdAt DESC LIMIT 10")
    List<FinancialRecord> recentActivity();
}