package com.kkimleang.rrms.service.file;

import com.kkimleang.rrms.entity.*;
import jakarta.annotation.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import lombok.extern.slf4j.*;
import org.apache.tomcat.util.http.fileupload.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;

@Slf4j
@Service
public class FileStorageService {
    private final String BASE_DIRECTORY = "uploads";
    private final Path root = Paths.get(BASE_DIRECTORY);

    @PostConstruct
    public void init() {
        try {
            log.info("Initialize folder for upload");
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public String save(User user, MultipartFile file) throws Exception {
        try {
            Path ad = Paths.get(BASE_DIRECTORY + "/" + user.getId());
            if (!Files.exists(ad)) {
                Files.createDirectories(ad);
            }
            String extension = getFileExtension(file);
            String uuid = UUID.randomUUID().toString();
            String newFilename = uuid + extension;
            Files.copy(file.getInputStream(), ad.resolve(newFilename));
            return newFilename;
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new FileAlreadyExistsException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getFileExtension(MultipartFile file) throws FileUploadException {
        String filename = Objects.requireNonNull(file.getOriginalFilename());
        if (filename.contains("..")) {
            throw new FileUploadException("Sorry! Filename contains invalid path sequence " + filename);
        } else if (file.isEmpty()) {
            throw new FileUploadException("Sorry! File is empty " + filename);
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            throw new FileUploadException("Invalid file name");
        }
        return filename.substring(dotIndex);
    }

    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public List<Resource> load(List<String> files) {
        List<Resource> resources = new ArrayList<>();
        for (String file : files) {
            try {
                resources.add(load(file));
            } catch (Exception e) {
                log.error("Load File Error: {}", e.getMessage());
            }
        }
        return resources;
    }

    public void delete(String filename) {
        try {
            Files.delete(root.resolve(filename));
        } catch (Exception e) {
            throw new RuntimeException("Could not delete the file!");
        }
    }
}
