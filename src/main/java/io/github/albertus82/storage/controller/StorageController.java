package io.github.albertus82.storage.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import io.github.albertus82.storage.dto.ResourceDTO;
import io.github.albertus82.storage.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Validated
public class StorageController {

	private static final short FILENAME_MAXLENGTH = 1024;

	private final StorageService storageService;

	@GetMapping
	public List<ResourceDTO> get(@RequestParam(name = "patterns", defaultValue = "") String[] patterns, HttpServletRequest request) throws IOException {
		return storageService.list(patterns).stream().map(resource -> {
			try {
				return new ResourceDTO(resource, new URL(request.getRequestURL().append("?filename=") + encodeFilename(resource.getFilename())));
			}
			catch (final MalformedURLException e) {
				throw new UncheckedIOException(e);
			}
		}).sorted().toList();
	}

	@GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, params = "filename")
	public ResponseEntity<Resource> get(@RequestParam(name = "filename", required = false) @Size(max = FILENAME_MAXLENGTH) String filename) throws IOException {
		if (filename == null || filename.isBlank()) {
			throw new IllegalArgumentException("filename must not be null or blank");
		}
		filename = filename.trim();
		final var resource = storageService.get(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + UriUtils.encodeFragment(filename.substring(filename.lastIndexOf('/') + 1), StandardCharsets.UTF_8)).contentLength(resource.contentLength()).lastModified(resource.lastModified()).body(resource);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResourceDTO> post(@RequestParam("file") MultipartFile file, @RequestParam(name = "filename", defaultValue = "") @Size(max = FILENAME_MAXLENGTH) String customFilename, HttpServletRequest request) throws IOException, URISyntaxException {
		final var filename = customFilename == null || customFilename.isBlank() ? file.getOriginalFilename() : customFilename;
		if (filename == null || filename.isBlank()) {
			throw new IllegalArgumentException("filename must not be null or blank");
		}
		final var resource = storageService.put(new InputStreamResource(file.getInputStream()), filename.trim());
		final var location = new URI(request.getRequestURL().append("?filename=") + encodeFilename(resource.getFilename()));
		return ResponseEntity.created(location).body(new ResourceDTO(resource, location.toURL()));
	}

	@PutMapping
	public ResourceDTO put(@RequestParam("filename") @NotBlank @Size(max = FILENAME_MAXLENGTH) String oldFilename, @RequestParam("new_filename") @NotBlank @Size(max = FILENAME_MAXLENGTH) String newFilename, HttpServletRequest request) throws IOException {
		final var resource = storageService.move(oldFilename.trim(), newFilename.trim());
		return new ResourceDTO(resource, new URL(request.getRequestURL().append("?filename=") + encodeFilename(resource.getFilename())));
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@RequestParam("filename") @NotBlank @Size(max = FILENAME_MAXLENGTH) String filename) throws IOException {
		storageService.delete(filename.trim());
	}

	private static String encodeFilename(final String filename) {
		Objects.requireNonNull(filename, "filename must not be null");
		return UriUtils.encodeQueryParam(filename, StandardCharsets.UTF_8);
	}

}
