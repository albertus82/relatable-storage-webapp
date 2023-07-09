package io.github.albertus82.storage.dto;

import io.github.albertus82.storage.constants.UserRole;
import io.github.albertus82.storage.entity.User;
import lombok.NonNull;
import lombok.Value;

@Value
public class UserDTO {

	String username;
	String password;
	UserRole role;

	public UserDTO(@NonNull final User entity) {
		this.username = entity.getUsername();
		this.password = entity.getPassword();
		this.role = entity.getRole();
	}

}
