package com.kkimleang.rrms.repository.file;

import com.kkimleang.rrms.entity.PropRoomPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PropRoomPictureRepository extends JpaRepository<PropRoomPicture, UUID> {
    Set<PropRoomPicture> findByFilenameIn(Set<String> roomPictures);

    Set<PropRoomPicture> findByIdIn(Set<UUID> pictures);

    Optional<PropRoomPicture> findByFilename(String filename);
}
