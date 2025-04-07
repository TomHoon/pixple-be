package com.example.demo.controller.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
public class UploadController {

  private static final String UPLOAD_DIR = "uploads/";

  @GetMapping("/uploadtest")
  public String test() {
    return "test";
  }

  @PostMapping("/upload")
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

    Path uploadPath = Paths.get(UPLOAD_DIR);

    try {
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      String filename = file.getOriginalFilename();
      Path filePath = uploadPath.resolve(filename);

      file.transferTo(filePath);

    } catch (IOException e) {
      return ResponseEntity.status(500).body("error!");
    }

    return ResponseEntity.ok("okay");
  }
}
