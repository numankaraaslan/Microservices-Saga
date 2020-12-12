package com.aldimbilet.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aldimbilet.userservice.model.ABUser;
import com.aldimbilet.userservice.repo.UserRepository;

@Service
public class UserService implements UserDetailsService
{
	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder bCryptPasswordEncoder;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username)
	{
		ABUser user = userRepository.findByUsername(username);
		UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
		builder.password(user.getPassword());
		builder.authorities(user.getRoles());
		return builder.build();
	}

	public boolean save(ABUser user)
	{
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public ABUser findByUsername(String username)
	{
		return userRepository.findByUsername(username);
	}

	public ABUser findById(Long userId)
	{
		return userRepository.findById(userId);
	}
}
