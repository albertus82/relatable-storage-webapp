package io.github.albertus82.storage.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
				return new ResourceDTO(resource, new URL(request.getRequestURL() + "/" + resource.getFilename()));
			}
			catch (final MalformedURLException e) {
				throw new UncheckedIOException(e);
			}
		}).sorted().toList();
	}

	@GetMapping("/{filename}")
	public ResponseEntity<Resource> get(@PathVariable("filename") @NotBlank @Size(max = FILENAME_MAXLENGTH) String filename) throws IOException {
		final var resource = storageService.get(filename.trim());
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + '"').contentLength(resource.contentLength()).lastModified(resource.lastModified()).body(resource);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResourceDTO> post(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException, URISyntaxException {
		final var resource = storageService.put(new InputStreamResource(file.getInputStream()), file.getOriginalFilename().trim());
		final var location = new URI(request.getRequestURL().append('/') + resource.getFilename());
		return ResponseEntity.created(location).body(new ResourceDTO(resource, location.toURL()));
	}

	@PutMapping("/{filename}")
	public ResourceDTO put(@PathVariable("filename") @NotBlank @Size(max = FILENAME_MAXLENGTH) String oldFilename, @RequestParam("new_filename") @NotBlank @Size(max = FILENAME_MAXLENGTH) String newFilename, HttpServletRequest request) throws IOException {
		final var resource = storageService.move(oldFilename.trim(), newFilename.trim());
		final var requestURL = request.getRequestURL();
		return new ResourceDTO(resource, new URL(requestURL.substring(0, requestURL.lastIndexOf("/") + 1) + resource.getFilename()));
	}

	@DeleteMapping("/{filename}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("filename") @NotBlank @Size(max = FILENAME_MAXLENGTH) String filename) throws IOException {
		storageService.delete(filename.trim());
	}

}
