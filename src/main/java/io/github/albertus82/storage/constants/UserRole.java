package io.github.albertus82.storage.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {

	RO(UserRoleNames.RO),
	RW(UserRoleNames.RW);

	@NonNull
	private final String name;

	@Override
	public String toString() {
		return name;
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class UserRoleNames {

		public static final String RO = "RO";
		public static final String RW = "RW";

	}

}
