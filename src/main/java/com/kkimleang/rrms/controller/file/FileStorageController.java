package com.kkimleang.rrms.controller.file;

import com.kkimleang.rrms.annotation.CurrentUser;
import com.kkimleang.rrms.entity.PropRoomPicture;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.exception.FileStorageException;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.payload.Response;
import com.kkimleang.rrms.payload.response.file.FileResponse;
import com.kkimleang.rrms.service.file.FileStorageService;
import com.kkimleang.rrms.service.file.PropRoomPictureService;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import com.kkimleang.rrms.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.kkimleang.rrms.constant.FileLogErrorMessage.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileStorageController {
    private final FileStorageService fileStorageService;
    private final PropRoomPictureService propRoomPictureService;

    @GetMapping("/load/{id}")
    public Response<FileResponse> loadFile(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id) {
        try {
            PropRoomPicture picture = propRoomPictureService.findById(id);
            if (picture == null) {
                return Response.<FileResponse>notFound()
                        .setErrors(FILE_NOT_FOUND);
            }
            Resource resource = fileStorageService.load(user.getUser(), picture.getFilename());
            if (resource == null || !resource.exists()) {
                return Response.<FileResponse>notFound()
                        .setErrors(FILE_NOT_FOUND);
            }
            FileResponse fileResponse = FileResponse.fromPropRoomPicture(user.getUser(), picture);
            return Response.<FileResponse>ok()
                    .setPayload(fileResponse);
        } catch (ResourceNotFoundException e) {
            log.error(FILE_NOT_FOUND + ": {}", e.getMessage());
            return Response.<FileResponse>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            log.error(LOAD_FILE_ERROR + "{}", e.getMessage());
            return Response.<FileResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> loadView(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id) {
        try {
            PropRoomPicture picture = propRoomPictureService.findById(id);
            if (picture == null) {
                throw new ResourceNotFoundException("File", id);
            }
            Resource resource = fileStorageService.load(user.getUser(), picture.getFilename());
            if (resource == null || !resource.exists()) {
                throw new ResourceNotFoundException("File", picture.getFilename());
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/jpeg"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error(LOAD_FILE_ERROR + "{}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("authenticated")
    public Response<FileResponse> uploadFile(
            @CurrentUser CustomUserDetails user,
            @RequestParam("file") MultipartFile file) {
        try {
            if (user == null || user.getUser() == null) {
                return Response.<FileResponse>accessDenied()
                        .setErrors("User is not permitted to upload file.");
            }
            String filename = fileStorageService.save(user.getUser(), file);
            PropRoomPicture picture = propRoomPictureService.save(user.getUser().getId(), filename);
            if (picture == null) {
                return Response.<FileResponse>badRequest()
                        .setErrors("File cannot not uploaded: " + filename);
            }
            FileResponse fileResponse = FileResponse.fromPropRoomPicture(user.getUser(), picture);
            return Response.<FileResponse>ok()
                    .setPayload(fileResponse);
        } catch (FileStorageException e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage());
            return Response.<FileResponse>duplicateEntity()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage(), e);
            return Response.<FileResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @PostMapping("/uploads")
    @PreAuthorize("authenticated")
    public Response<Set<FileResponse>> uploadFiles(
            @CurrentUser CustomUserDetails user,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            if (user == null || user.getUser() == null) {
                return Response.<Set<FileResponse>>accessDenied()
                        .setErrors("User is not permitted to upload file.");
            }
            List<String> filenames = fileStorageService.saveAll(user.getUser(), files);
            Set<PropRoomPicture> pictures = propRoomPictureService.saveAll(user.getUser().getId(), filenames);
            if (pictures == null || pictures.isEmpty()) {
                return Response.<Set<FileResponse>>badRequest()
                        .setErrors("Files cannot not uploaded: " + filenames);
            }
            Set<FileResponse> fileResponses = FileResponse.fromPropRoomPictures(user.getUser(), pictures);
            return Response.<Set<FileResponse>>ok()
                    .setPayload(fileResponses);
        } catch (FileStorageException e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage());
            return Response.<Set<FileResponse>>duplicateEntity()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            log.error(FILE_STORAGE_ERROR + "{}", e.getMessage(), e);
            return Response.<Set<FileResponse>>exception()
                    .setErrors(e.getMessage());
        }
    }
}
