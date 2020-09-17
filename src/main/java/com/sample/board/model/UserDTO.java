package com.sample.board.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
	private int id;
	private String username;
	private String password;
	private String email;
	private String role;
	private String oauth;
	private Date createDate;
}
