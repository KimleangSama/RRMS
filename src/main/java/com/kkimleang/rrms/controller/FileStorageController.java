package com.kkimleang.rrms.controller;

import com.kkimleang.rrms.annotation.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.*;
import com.kkimleang.rrms.payload.response.file.*;
import com.kkimleang.rrms.service.file.*;
import com.kkimleang.rrms.service.user.*;
import java.nio.file.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.core.io.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileStorageController {
    private final PropertyRoomService propertyRoomService;
    private final FileStorageService fileStorageService;

    private final String FILE_NOT_FOUND = "File not found";

    @GetMapping("/load/{id}")
    public Response<FileResponse> loadFile(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id) {
        try {
            PropRoomPicture picture = propertyRoomService.findById(id);
            if (picture == null) {
                return Response.<FileResponse>notFound()
                        .setErrors(FILE_NOT_FOUND);
            }
            UUID userId = picture.getCreatedBy();
            Resource resource = fileStorageService.load(userId + "/" + picture.getFilename());
            if (resource == null || !resource.exists()) {
                return Response.<FileResponse>notFound()
                        .setErrors(FILE_NOT_FOUND);
            }
            if (user == null || user.getUser() == null) {
                return Response.<FileResponse>ok()
                        .setPayload(FileResponse.fromPropRoomPicture(null, picture));
            }
            FileResponse fileResponse = FileResponse.fromPropRoomPicture(user.getUser(), picture);
            return Response.<FileResponse>ok()
                    .setPayload(fileResponse);
        } catch (ResourceNotFoundException e) {
            log.error(FILE_NOT_FOUND + ": {}", e.getMessage());
            return Response.<FileResponse>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            log.error("Load File Error: {}", e.getMessage());
            return Response.<FileResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("authenticated")
    public Response<FileResponse> uploadFile(
            @CurrentUser CustomUserDetails user,
            @RequestParam("file") MultipartFile file) {
        final String FILE_UPLOAD_ERROR = "Upload File Error: {}";
        try {
            if (user == null || user.getUser() == null) {
                return Response.<FileResponse>accessDenied()
                        .setErrors("User is not permitted to upload file.");
            }
            String filename = fileStorageService.save(user.getUser(), file);
            PropRoomPicture picture = propertyRoomService.save(user.getUser().getId(), filename);
            if (picture == null) {
                return Response.<FileResponse>badRequest()
                        .setErrors("File cannot not uploaded: " + filename);
            }
            FileResponse fileResponse = FileResponse.fromPropRoomPicture(user.getUser(), picture);
            return Response.<FileResponse>ok()
                    .setPayload(fileResponse);
        } catch (FileAlreadyExistsException e) {
            log.error(FILE_UPLOAD_ERROR, e.getMessage());
            return Response.<FileResponse>duplicateEntity()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            log.error(FILE_UPLOAD_ERROR, e.getMessage());
            return Response.<FileResponse>exception()
                    .setErrors(e.getMessage());
        }
    }
}
