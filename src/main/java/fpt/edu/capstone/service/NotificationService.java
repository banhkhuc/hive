package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.Notification;
import fpt.edu.capstone.utils.ResponseDataPagination;

import java.util.Optional;


public interface NotificationService {
    Notification insertNotification(Notification notification);

    ResponseDataPagination getAllNotificationByUserId(Integer pageNo, Integer pageSize, long userId);

    Optional<Notification> findNotificationByReceiveIdAndTargetId(long receiverId, long targetId, long type);

    void updateIsSeen(long notificationId);

    Optional<Notification> findById(long notificationId);
}
