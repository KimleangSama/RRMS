package com.kkimleang.rrms.payload.response.file;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.file.*;
import com.kkimleang.rrms.exception.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class FileResponse {
    private UUID id;
    private String name;
    private UUID createdBy;
    private Instant createdAt;
    private Boolean hasPrivilege = false;
    private FileAccessibleType accessibleType;

    public static FileResponse fromPropRoomPicture(User user, PropRoomPicture picture) {
        FileResponse fileResponse = new FileResponse();
        fileResponse.setAccessibleType(picture.getFileAccessibleType());
        if (user != null && user.getId().equals(picture.getCreatedBy())) {
            fileResponse.setHasPrivilege(true);
            fileResponse.setAccessibleType(FileAccessibleType.OWNER);
        }
        if (fileResponse.getAccessibleType().equals(FileAccessibleType.PRIVATE)) {
            throw new ResourceNotFoundException("File", "filename", picture.getFilename());
        }
        fileResponse.setId(picture.getId());
        fileResponse.setName(picture.getFilename());
        fileResponse.setCreatedBy(picture.getCreatedBy());
        fileResponse.setCreatedAt(picture.getCreatedAt());
        return fileResponse;
    }

    public static Set<FileResponse> fromPropRoomPictures(User user, Set<PropRoomPicture> pictures) {
        Set<FileResponse> fileResponses = new HashSet<>();
        for (PropRoomPicture picture : pictures) {
            fileResponses.add(fromPropRoomPicture(user, picture));
        }
        return fileResponses;
    }

    public static FileResponse fromPropRoomPicture(PropRoomPicture picture) {
        FileResponse fileResponse = new FileResponse();
        fileResponse.setAccessibleType(picture.getFileAccessibleType());
        fileResponse.setId(picture.getId());
        fileResponse.setName(picture.getFilename());
        fileResponse.setCreatedBy(picture.getCreatedBy());
        fileResponse.setCreatedAt(picture.getCreatedAt());
        return fileResponse;
    }

    public static Set<FileResponse> fromPropRoomPictures(Set<PropRoomPicture> pictures) {
        Set<FileResponse> fileResponses = new HashSet<>();
        for (PropRoomPicture picture : pictures) {
            fileResponses.add(fromPropRoomPicture(picture));
        }
        return fileResponses;
    }
}
