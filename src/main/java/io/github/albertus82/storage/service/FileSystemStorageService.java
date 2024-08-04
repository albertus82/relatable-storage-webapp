package io.github.albertus82.storage.service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path directory;

	public FileSystemStorageService(@Value("${relatable-storage.directory}") Path directory) throws IOException {
		this.directory = directory.toRealPath();
	}

	@Override
	public List<Resource> list(final String... patterns) throws IOException {
		try (final var stream = Files.list(directory)) {
			// TODO patterns https://stackoverflow.com/a/20960359/3260495
			return stream.map(e -> new FileSystemResource(e) {
				@Override
				public URI getURI() throws IOException {
					return URI.create(super.getURI().toString().replaceFirst(directory.toString(), ""));
				}
			}).collect(Collectors.toList());
		}
	}

	@Override
	public Resource get(final String filename) throws IOException {
		return new FileSystemResource(createPath(filename));
	}

	@Override
	public Resource put(final Resource resource, final String filename) throws IOException {
		final var path = createPath(filename);
		final var parent = path.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		try (final var in = resource.getInputStream()) {
			Files.copy(in, path);
			return new FileSystemResource(path);
		}
	}

	@Override
	public Resource move(final String oldFilename, final String newFilename) throws IOException {
		final var oldPath = createPath(oldFilename);
		final var newPath = createPath(newFilename);
		final var parent = newPath.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		try {
			return new FileSystemResource(Files.move(oldPath, newPath, StandardCopyOption.ATOMIC_MOVE));
		}
		catch (final AtomicMoveNotSupportedException e) {
			return new FileSystemResource(Files.move(oldPath, newPath));
		}
	}

	@Override
	public void delete(final String filename) throws IOException {
		Files.delete(createPath(filename));
	}

	private Path createPath(final String filename) throws IOException {
		final var path = Path.of(directory.toString(), filename).toRealPath(LinkOption.NOFOLLOW_LINKS);
		if (!path.startsWith(directory)) {
			throw new SecurityException(filename);
		}
		return path;
	}

//	public class MyFileSystemResource extends FileSystemResource {
//
//		public MyFileSystemResource(Path filePath) {
//			super(filePath);
//		}
//
//		@Override
//		public URI getURI() throws IOException {
//			return URI.create(super.getURI().toString().replaceFirst(directory.toString(), ""));
//		}
//	}

}
