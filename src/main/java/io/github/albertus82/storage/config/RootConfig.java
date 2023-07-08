package io.github.albertus82.storage.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import io.github.albertus82.storage.StorageOperations;
import io.github.albertus82.storage.io.Compression;
import io.github.albertus82.storage.jdbc.RelaTableStorage;
import io.github.albertus82.storage.jdbc.read.DirectBlobExtractor;
import io.github.albertus82.storage.jdbc.write.FileBufferedBinaryStreamProvider;

@Configuration
public class RootConfig {

	@Bean
	StorageOperations storage(JdbcOperations jdbcOperations, @Value("${relatable-storage.schema-name:#{null}}") String schemaName, @Value("${relatable-storage.table-name}") String tableName, @Value("${relatable-storage.compression:LOW}") Compression compression) {
		return new RelaTableStorage(jdbcOperations, tableName, new DirectBlobExtractor(), new FileBufferedBinaryStreamProvider(), compression, false, schemaName, null) {
			private final Logger log = LoggerFactory.getLogger(getClass());

			@Override
			protected void logStatement(final String sql) {
				log.info("{}", sql);
			}
		};
	}

	@Bean
	JdbcOperations jdbcOperations(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

}
