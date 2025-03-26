package com.offerblock.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.offerblock.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime requestDate;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private ProjectApprover approver;

    @OneToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    private String requestedBy; 
}
