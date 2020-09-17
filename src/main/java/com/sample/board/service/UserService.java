package com.sample.board.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sample.board.mapper.UserDAO;
import com.sample.board.model.UserDTO;

@Service
public class UserService {
	
	@Autowired
	UserDAO userDao;
	
	@Autowired
	BCryptPasswordEncoder encoder;
	
	public void update(UserDTO user) {
		String bcPassword = encoder.encode(user.getPassword());
		user.setPassword(bcPassword);
		userDao.update(user);
	}
	
	public UserDTO login2(UserDTO user) {
		return userDao.login2(user);
	}
	
	public UserDTO login(String username) {
		UserDTO result = userDao.login(username);
		return userDao.login(username);
	}
	
	public void insert(UserDTO user) {
		String bcPassword = encoder.encode(user.getPassword());
		user.setPassword(bcPassword);
		userDao.insert(user);
	}
}
