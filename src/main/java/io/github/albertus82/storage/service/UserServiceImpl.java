package io.github.albertus82.storage.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.albertus82.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserDetailsService userDetailsService() {
		return username -> userRepository.findByUsernameIgnoringCase(username).map(user -> new User(user.getUsername(), user.getPassword(), Collections.emptySet())).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
	}

}
