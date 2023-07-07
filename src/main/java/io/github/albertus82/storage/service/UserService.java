package io.github.albertus82.storage.service;

import java.util.Optional;

import io.github.albertus82.storage.dto.UserDTO;

public interface UserService {

	Optional<UserDTO> findByUsername(String username);

}
