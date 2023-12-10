package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Notification;
import fpt.edu.capstone.repository.NotificationRepository;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository mockNotificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationServiceImplUnderTest;

    private Notification notificationA = new Notification(1L, 1L, 1L,
            LocalDateTime.now(), "content", false, false, 1L);
    @Test
    public void testInsertNotification() {
        final Notification notification = notificationA;
        final Notification notification1 = notificationA;
        when(mockNotificationRepository.save(any(Notification.class))).thenReturn(notification1);
        final Notification result = notificationServiceImplUnderTest.insertNotification(notification);
    }

    @Test
    public void testGetAllNotificationByUserId() {
        final Page<Notification> notifications = new PageImpl<>(Arrays.asList(notificationA));
        when(mockNotificationRepository.getAllNotificationByUserId(any(Pageable.class), eq(1L)))
                .thenReturn(notifications);
        final ResponseDataPagination result = notificationServiceImplUnderTest.getAllNotificationByUserId(1, 11, 11L);
    }

    @Test
    public void testGetAllNotificationByUserId_NotificationRepositoryReturnsNoItems() {
        when(mockNotificationRepository.getAllNotificationByUserId(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final ResponseDataPagination result = notificationServiceImplUnderTest.getAllNotificationByUserId(1, 11, 11L);
    }

    @Test
    public void testFindNotificationByReceiveIdAndTargetId() {
        final Optional<Notification> notification = Optional.of(notificationA);
        when(mockNotificationRepository.findNotificationByReceiveIdAndTargetId(1L, 1L, 1L)).thenReturn(notification);
        final Optional<Notification> result = notificationServiceImplUnderTest.findNotificationByReceiveIdAndTargetId(
                1L, 1L, 1L);
    }

    @Test
    public void testFindNotificationByReceiveIdAndTargetId_NotificationRepositoryReturnsAbsent() {
        when(mockNotificationRepository.findNotificationByReceiveIdAndTargetId(1L, 1L, 1L))
                .thenReturn(Optional.empty());
        final Optional<Notification> result = notificationServiceImplUnderTest.findNotificationByReceiveIdAndTargetId(
                1L, 1L, 1L);

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    public void testUpdateIsSeen() {
        notificationServiceImplUnderTest.updateIsSeen(1L);
        verify(mockNotificationRepository).updateIsSenn(true, 1L);
    }

    @Test
    public void testFindById() throws Exception {
        final Optional<Notification> notification = Optional.of(notificationA);
        when(mockNotificationRepository.findById(1L)).thenReturn(notification);
        final Optional<Notification> result = notificationServiceImplUnderTest.findById(1L);
    }

    @Test
    public void testFindById_NotificationRepositoryReturnsAbsent() {
        when(mockNotificationRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Notification> result = notificationServiceImplUnderTest.findById(1L);
        assertThat(result).isEmpty();
    }
}
