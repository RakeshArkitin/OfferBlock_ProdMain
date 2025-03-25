package com.offerblock.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "project_metrics")
public class ProjectMetrics {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int offerCount = 0;
	private int hiringCount = 0;

	@OneToOne
	@JoinColumn(name = "project_id", referencedColumnName = "projectId", nullable = false)
	private Project project;

	public ProjectMetrics(Project project, int hiringCount) {
		this.project = project;
		this.hiringCount = hiringCount;
	}

	public void incrementOfferCount() {
		this.offerCount += 1;
	}

	public void incrementHiringCount() {
		this.hiringCount += 1;
	}

	@PrePersist
	public void prePersist() {
		if (this.hiringCount == 0) {
			this.hiringCount = 0;
		}
		if (this.offerCount == 0) {
			this.offerCount = 0;
		}
	}
}
