package com.sample.board.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reply2DTO {
	private int id;
	private String content;
	private Date createDate;
	private int boardId;
	private int userId;
}
