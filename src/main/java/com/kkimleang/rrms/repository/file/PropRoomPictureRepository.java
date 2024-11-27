package com.kkimleang.rrms.repository.file;

import com.kkimleang.rrms.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface PropRoomPictureRepository extends JpaRepository<PropRoomPicture, UUID> {
    Set<PropRoomPicture> findByFilenameIn(Set<String> roomPictures);

    Set<PropRoomPicture> findByIdIn(Set<UUID> pictures);

    Optional<PropRoomPicture> findByFilename(String filename);
}
