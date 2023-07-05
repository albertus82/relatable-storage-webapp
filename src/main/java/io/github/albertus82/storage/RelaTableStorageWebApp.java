package io.github.albertus82.storage;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.EnumerablePropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class RelaTableStorageWebApp extends SpringBootServletInitializer {

	public RelaTableStorageWebApp() {
		setRegisterErrorPageFilter(false);
	}

	public static void main(final String... args) {
		final var context = SpringApplication.run(RelaTableStorageWebApp.class, args);
		log.atDebug().log(() -> "Beans:" + System.lineSeparator() + Arrays.stream(context.getBeanDefinitionNames()).sorted().map(beanName -> '\t' + beanName + " ==>> " + context.getType(beanName)).collect(Collectors.joining(System.lineSeparator())));
		final var env = context.getEnvironment();
		log.atDebug().log(() -> "Properties:" + System.lineSeparator() + StreamSupport.stream(env.getPropertySources().spliterator(), false).filter(EnumerablePropertySource.class::isInstance).map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames()).flatMap(Arrays::stream).distinct().filter(prop -> !(prop.contains("credentials") || prop.contains("password"))).map(prop -> '\t' + prop + " ==>> " + env.getProperty(prop)).sorted().collect(Collectors.joining(System.lineSeparator())));
	}

}
