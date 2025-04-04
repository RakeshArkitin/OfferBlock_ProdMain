package com.offerblock.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.offerblock.enums.BudgetStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Budget {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "TotalDisbursement expenses cannot be null")
	@Min(value = 0, message = "TotalDisbursement expenses must be a positive number")
	private Double totalDisbursement;

	@NotNull(message = "Onboarding expenses cannot be null")
	@Min(value = 0, message = "Onboarding expenses must be a positive number")
	private Double onboardingExpenses;

	@NotNull(message = "MiscellaneousExpenses expenses cannot be null")
	@Min(value = 0, message = "MiscellaneousExpenses expenses must be a positive number")
	private Double miscellaneousExpenses;
	
	@Enumerated(EnumType.STRING)
	private BudgetStatus status = BudgetStatus.PENDING;

	@OneToOne(mappedBy = "budget", cascade = CascadeType.ALL)
	@JsonIgnore
	private Project project;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@CreationTimestamp
	private LocalDateTime updatedAt;
	
	public void setStatus(BudgetStatus status) {
		this.status = status;
	}
	
	public Budget(Budget budget) {
		this.id = budget.getId();
		this.totalDisbursement = budget.getTotalDisbursement();
		this.onboardingExpenses = budget.getOnboardingExpenses();
		this.miscellaneousExpenses = budget.getMiscellaneousExpenses();
		this.status = budget.getStatus();
	}
}
