package io.github.albertus82.storage.service;

import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.albertus82.storage.StorageOperations;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

	private final StorageOperations storage;

	@Override
	public List<Resource> list(String... patterns) throws IOException {
		return storage.list(patterns);
	}

	@Override
	public Resource get(String filename) throws IOException {
		return storage.get(filename);
	}

	@Override
	@Transactional
	public Resource put(Resource resource, String filename) throws IOException {
		return storage.put(resource, filename);
	}

	@Override
	@Transactional
	public Resource move(String oldFilename, String newFilename) throws IOException {
		return storage.move(oldFilename, newFilename, StandardCopyOption.ATOMIC_MOVE);
	}

	@Override
	@Transactional
	public void delete(String filename) throws IOException {
		storage.delete(filename);
	}

}
