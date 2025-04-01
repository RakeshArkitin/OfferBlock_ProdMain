package com.offerblock.dto;

import com.offerblock.entity.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PositionDTO {

	private String positionName;
	private int vacancies;
	
	public PositionDTO(Position position) {
        this.positionName = position.getPositionName();  
        this.vacancies = position.getVacancies();  
    }
}
