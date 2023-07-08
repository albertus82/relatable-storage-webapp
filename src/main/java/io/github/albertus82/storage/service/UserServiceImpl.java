package io.github.albertus82.storage.service;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.albertus82.storage.config.CacheConfig;
import io.github.albertus82.storage.dto.UserDTO;
import io.github.albertus82.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	@Cacheable(cacheManager = CacheConfig.CACHE_MANAGER, cacheNames = "users")
	public Optional<UserDTO> findByUsername(final String username) {
		return userRepository.findByUsernameIgnoringCase(username).map(UserDTO::new);
	}

}
