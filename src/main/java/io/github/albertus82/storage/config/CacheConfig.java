package io.github.albertus82.storage.config;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Weigher;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

	public static final String CACHE_MANAGER = "cacheManager";

	private static final String CAFFEINE = "caffeine";

	@Bean(CACHE_MANAGER)
	@Primary
	CacheManager shortCacheManager(@Qualifier(CAFFEINE) Caffeine<Object, Object> caffeine) {
		final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setCaffeine(caffeine);
		return caffeineCacheManager;
	}

	@Bean(CAFFEINE)
	@Primary
	Caffeine<Object, Object> shortCaffeine(@Value("${cache.expireAfterWrite.minutes:1}") short expireAfterWriteMinutes, @Value("${cache.maximumWeight:1000}") int maximumWeight) {
		return Caffeine.newBuilder().expireAfterWrite(expireAfterWriteMinutes, TimeUnit.MINUTES).weigher(weigher()).maximumWeight(maximumWeight).removalListener(removalListener());
	}

	private static Weigher<? super Object, ? super Object> weigher() {
		return (k, v) -> v instanceof Collection<?> ? ((Collection<?>) v).size() : 1;
	}

	private static RemovalListener<? super Object, ? super Object> removalListener() {
		return (k, v, c) -> log.trace("Removed element {} from cache ({}).", k, c);
	}

}
