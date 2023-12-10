package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.user.UpdateUserRequest;
import fpt.edu.capstone.entity.Notification;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.service.NotificationService;
import fpt.edu.capstone.service.UserService;
import fpt.edu.capstone.utils.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;

    private final NotificationService notificationService;



    @PostMapping("/update/{userId}")
    @Operation(summary = "update user")
    public ResponseData login(@RequestBody UpdateUserRequest request, @PathVariable long userId) throws Exception {
        try {
            Users user = userService.getUserById(userId);
            user.setAvatar(request.getAvatar());
            userService.saveUser(user);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.UPDATE_SUCCESSFULLY, user);

        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/get-all-notification")
    ResponseDataPagination getAllNotification(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam long userId) {
        return notificationService.getAllNotificationByUserId(pageNo, pageSize, userId);
    }

    @GetMapping("/user-infor/{id}")
    public ResponseData getUserInfor(@PathVariable long id){
        try {
            Users users =  userService.getUserById(id);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.GET_LIST_SUCCESS, users);
        } catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PutMapping("/update-notification-seen")
    public ResponseData updateNotificationSeen(@RequestParam long notificationId) {
        try {
            Optional<Notification> notification = notificationService.findById(notificationId);
            if(!notification.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Không tìm thấy thông báo này");
            }
            notificationService.updateIsSeen(notificationId);
            Optional<Notification> notification1 = notificationService.findById(notificationId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Cập nhật đã xem thông báo thành công", notification1.get());
        }catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }


}
