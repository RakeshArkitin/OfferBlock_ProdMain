package com.offerblock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
