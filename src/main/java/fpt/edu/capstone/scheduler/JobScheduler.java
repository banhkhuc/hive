package fpt.edu.capstone.scheduler;

import fpt.edu.capstone.entity.Job;
import fpt.edu.capstone.entity.Notification;
import fpt.edu.capstone.entity.Payment;
import fpt.edu.capstone.entity.Recruiter;
import fpt.edu.capstone.service.JobService;
import fpt.edu.capstone.service.NotificationService;
import fpt.edu.capstone.service.PaymentService;
import fpt.edu.capstone.service.RecruiterService;
import fpt.edu.capstone.utils.LocalDateTimeUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
@AllArgsConstructor
public class JobScheduler {
    private final PaymentService paymentService;

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private final JobService jobService;

    private final NotificationService notificationService;

    private final RecruiterService recruiterService;

    @Scheduled(fixedDelay = 9000000)
    public void checkPaymentExpire(){
        logger.info("-------------->START-CHECK_PAYMENT_EXPIRE:" + new Date());
        List <Payment> paymentList = paymentService.findAll();
        for (Payment payment: paymentList){
            if(LocalDateTimeUtils.checkExpireTime(payment.getExpiredDate())){
                payment.setExpiredStatus(true);

                //Add notification
                Optional<Job> j = jobService.findById(payment.getJobId());
                if(j.isPresent()) {
                    Optional<Recruiter> r = recruiterService.findById(payment.getRecruiterId());
                    if(r.isPresent() && !notificationService.findNotificationByReceiveIdAndTargetId(r.get().getUserId(), payment.getId(), 4).isPresent()) {
                        String content = "Gói nâng cấp bạn đã mua cho công việc " + j.get().getJobName() + " đã hết hạn";
                        Notification notification = new Notification(0, r.get().getUserId(), 4, LocalDateTime.now(), content, false, false, payment.getId());
                        notificationService.insertNotification(notification);
                    }
                }

            }
            paymentService.save(payment);
        }
        logger.info("-------------->END-CHECK_PAYMENT_EXPIRE:" + new Date());
    }

    @Scheduled(fixedDelay = 86400000)
    public void checkJobExpired() {
        logger.info("-------------->START-CHECK_JOB_EXPIRE:" + new Date());
        List<Job> jobList = jobService.findAll();
        for (Job job : jobList) {
            if (LocalDateTimeUtils.checkExpireTime(job.getEndDate())) {
                job.setIsDeleted(1);
            }
            jobService.saveJob(job);
        }
        logger.info("-------------->END-CHECK_JOB_EXPIRE:" + new Date());
    }
}
