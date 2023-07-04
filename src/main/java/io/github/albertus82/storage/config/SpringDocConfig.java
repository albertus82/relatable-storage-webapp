package io.github.albertus82.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@ComponentScan("org.springdoc")
public class SpringDocConfig {

	private final String groupId;
	private final String artifactId;
	private final String version;

	public SpringDocConfig(@Value("${project.groupId}") String groupId, @Value("${project.artifactId}") String artifactId, @Value("${project.version}") String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	@Bean
	OpenAPI openAPI() {
		return new OpenAPI().components(new Components()).info(new Info().version(version).title(artifactId).description(groupId + ':' + artifactId + ':' + version));
	}

}
