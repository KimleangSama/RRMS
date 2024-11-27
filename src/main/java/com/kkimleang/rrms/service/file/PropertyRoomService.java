package com.kkimleang.rrms.service.file;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.repository.file.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyRoomService {
    private final PropRoomPictureRepository propertyRoomRepository;

    public PropRoomPicture findById(UUID id) {
        try {
            PropRoomPicture picture = propertyRoomRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Picture", "id", id));
            if (picture == null) {
                throw new ResourceNotFoundException("Picture", "id", id);
            }
            return picture;
        } catch (ResourceNotFoundException e) {
            log.error("Picture Not Found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Find Picture Error: {}", e.getMessage());
            throw e;
        }
    }

    public PropRoomPicture save(UUID user, String filename) {
        try {
            PropRoomPicture picture = new PropRoomPicture();
            picture.setFilename(filename);
            picture.setCreatedBy(user);
            return propertyRoomRepository.save(picture);
        } catch (Exception e) {
            log.error("Save Property Picture Error: {}", e.getMessage());
            throw e;
        }
    }
}
