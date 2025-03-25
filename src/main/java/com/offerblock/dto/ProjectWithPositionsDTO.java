package com.offerblock.dto;

import java.util.List;

public class ProjectWithPositionsDTO {
	
    private Long projectId;
    private String projectName;
    private List<PositionDTO> positions;

    public ProjectWithPositionsDTO(Long projectId, String projectName, List<PositionDTO> positions) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.positions = positions;
    }

    // Getters and Setters
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<PositionDTO> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionDTO> positions) {
        this.positions = positions;
    }
}

