package com.offerblock.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetDTO {

    private Long id;
    private Double totalDisbursement;
    private Double onboardingExpenses;
    private Double miscellaneousExpenses;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    

}
