package com.sample.board.config.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sample.board.mapper.UserDAO;
import com.sample.board.model.UserDTO;

@Service
public class PrincipalDetailService implements UserDetailsService {

	@Autowired
	UserDAO userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDTO user = userDao.login(username);
		if(user == null) throw new UsernameNotFoundException(username);
		return new PrincipalDetail(user);
	}

}
