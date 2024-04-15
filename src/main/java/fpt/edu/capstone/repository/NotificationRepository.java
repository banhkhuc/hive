package fpt.edu.capstone.repository;

import com.amazonaws.services.dynamodbv2.xspec.N;
import fpt.edu.capstone.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

        @Query(value = "Select * from notification WHERE receiver_id = ?1 order by date desc", nativeQuery = true)
    Page<Notification> getAllNotificationByUserId(Pageable pageable, long userId);

    @Query(value = "Select * from notification Where receiver_id = ?1 and target_id = ?2 and type = ?3", nativeQuery = true)
    Optional<Notification> findNotificationByReceiveIdAndTargetId(long receiverId, long targetId, long type);

    @Modifying
    @Transactional
    @Query(value = "update notification set is_seen = ?1 where id = ?2", nativeQuery = true)
    void updateIsSenn(boolean isSeen, long notificationId);
}
