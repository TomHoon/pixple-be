package com.example.demo.controller.upload;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class DownloadController {

  @GetMapping("/download/{filename}")
  public ResponseEntity<Resource> downloadFile(@PathVariable("filename") String filename) throws Exception {
    Path filePath = Paths.get("uploads").resolve(filename).normalize();

    Resource resource = new UrlResource(filePath.toUri());

    if (!resource.exists()) {
      throw new FileNotFoundException(filename + "이 없습니다.");
    }

    String contentType = Files.probeContentType(filePath);
    if (contentType == null)
      contentType = "application/octet-stream";

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);

  }
}
