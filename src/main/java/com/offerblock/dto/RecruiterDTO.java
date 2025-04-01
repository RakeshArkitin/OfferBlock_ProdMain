package com.offerblock.dto;

import com.offerblock.entity.AssignedRecruiter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruiterDTO {

    private String id;
    private String name;
    private String designation;

    // Constructor that takes AssignedRecruiter entity
    public RecruiterDTO(AssignedRecruiter assignedRecruiter) {
        this.id = assignedRecruiter.getId();  // Assuming getId() method exists in AssignedRecruiter
        this.name = assignedRecruiter.getName();  // Assuming getName() method exists in AssignedRecruiter
        this.designation = assignedRecruiter.getDesiganation();  // Assuming getDesignation() method exists in AssignedRecruiter
    }

    // Constructor that takes fields directly
    public RecruiterDTO(String id, String name, String designation) {
        this.id = id;
        this.name = name;
        this.designation = designation;
    }

    // Default constructor
    public RecruiterDTO() {}
}
