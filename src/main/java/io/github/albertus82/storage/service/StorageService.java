package io.github.albertus82.storage.service;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.util.List;

import org.springframework.core.io.Resource;

public interface StorageService {

	List<Resource> list(String... patterns) throws IOException;

	Resource get(String filename) throws NoSuchFileException, IOException;

	Resource put(Resource resource, String filename, OpenOption... options) throws FileAlreadyExistsException, IOException;

	Resource move(String oldFilename, String newFilename) throws NoSuchFileException, FileAlreadyExistsException, IOException;

	void delete(String filename) throws NoSuchFileException, IOException;

}
