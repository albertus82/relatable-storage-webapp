package io.github.albertus82.storage.dto;

import io.github.albertus82.storage.entity.User;
import lombok.Value;

@Value
public class UserDTO {

	String username;
	String password;

	public UserDTO(final User entity) {
		this.username = entity.getUsername();
		this.password = entity.getPassword();
	}

}
