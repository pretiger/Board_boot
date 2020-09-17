package com.sample.board.mapper;

import com.sample.board.model.UserDTO;

public interface UserDAO {

	public void update(UserDTO user);
	public UserDTO login2(UserDTO user);
	public UserDTO login(String username);
	public void insert(UserDTO user);
}
