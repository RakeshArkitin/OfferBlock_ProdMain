package com.offerblock.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruiterDTO {
	private String id;
	private String name;
	private String designation;

	public RecruiterDTO(String id, String name, String designation) {
		super();
		this.id = id;
		this.name = name;
		this.designation = designation;
	}

	public RecruiterDTO() {
		super();
	}

}
