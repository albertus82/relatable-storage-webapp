package io.github.albertus82.storage.config;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import io.github.albertus82.storage.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, @Value("${http.hsts.enabled:true}") boolean hstsEnabled, @Value("${http.hsts.max-age:#{null}}") Integer hstsMaxAgeSeconds, @Value("${http.hsts.include-sub-domains:#{null}}") Boolean hstsIncludeSubDomains, @Value("${http.hsts.preload:#{null}}") Boolean hstsPreload) throws Exception {
		http.authorizeHttpRequests(registry -> registry.anyRequest().authenticated()).httpBasic(Customizer.withDefaults()).csrf(csrf -> csrf.disable());
		http.headers(headers -> headers.httpStrictTransportSecurity(hsts -> {
			if (hstsEnabled) {
				if (hstsMaxAgeSeconds != null) {
					hsts.maxAgeInSeconds(hstsMaxAgeSeconds);
				}
				if (hstsIncludeSubDomains != null) {
					hsts.includeSubDomains(hstsIncludeSubDomains);
				}
				if (hstsPreload != null) {
					hsts.preload(hstsPreload);
				}
			}
			else {
				hsts.disable();
			}
		}));
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		final var authProvider = new DaoAuthenticationProvider(passwordEncoder);
		authProvider.setUserDetailsService(userDetailsService);
		return authProvider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	UserDetailsService userDetailsService(UserService userService, @Value("${http.auth.fail-delay-millis:4000}") short delay) {
		return username -> userService.findByUsername(username).map(user -> new User(user.getUsername(), user.getPassword(), Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString())))).orElseGet(() -> {
			try {
				TimeUnit.MILLISECONDS.sleep(delay);
			}
			catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			throw new UsernameNotFoundException("Invalid credentials");
		});
	}

}
