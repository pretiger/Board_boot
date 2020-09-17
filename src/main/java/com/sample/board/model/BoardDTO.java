package com.sample.board.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
	private int num;
	private String writer;
	private String subject;
	private String content;
	private int subgroup;
	private int substep;
	private int sublevel;
	private int viewcount;
	private Date regdate;
	private String role;
	private String email;
	private int count;
}
