package com.kkimleang.rrms.repository.room;

import com.kkimleang.rrms.entity.Room;
import com.kkimleang.rrms.entity.RoomPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoomPictureRepository extends JpaRepository<RoomPicture, UUID> {
    Set<RoomPicture> findAllByPictureURLIn(Set<String> pictureURLs);
}
