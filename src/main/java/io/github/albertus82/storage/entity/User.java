package io.github.albertus82.storage.entity;

import io.github.albertus82.storage.constants.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "USERS")
public class User {

	@Id
	@Column(name = "USERNAME", nullable = false, unique = true, length = 128)
	private String username;

	@Column(name = "PASSWORD", nullable = false, length = 60)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "ROLE", nullable = false, length = 2)
	private UserRole role;

}
