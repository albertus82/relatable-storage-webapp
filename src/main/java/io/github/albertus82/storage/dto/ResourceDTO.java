package io.github.albertus82.storage.dto;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResourceDTO implements Comparable<ResourceDTO> {

	String filename;
	long contentLength;
	Date lastModified;
	URI uri;
	URL url;

	@SneakyThrows(IOException.class)
	public ResourceDTO(@NonNull final Resource resource, @NonNull final URL url) {
		this.filename = resource.getFilename();
		this.contentLength = resource.contentLength();
		this.lastModified = new Date(resource.lastModified());
		this.uri = resource.getURI();
		this.url = url;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ResourceDTO)) {
			return false;
		}
		ResourceDTO other = (ResourceDTO) obj;
		return Objects.equals(filename, other.filename);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filename);
	}

	@Override
	public int compareTo(final ResourceDTO o) {
		return filename.compareTo(o.filename);
	}

}
