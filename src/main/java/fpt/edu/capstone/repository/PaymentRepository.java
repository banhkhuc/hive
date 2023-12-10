package fpt.edu.capstone.repository;

import com.twilio.twiml.voice.Pay;
import fpt.edu.capstone.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p where (p.recruiterId =:recruiterId or 0 =:recruiterId) " +
            "and (p.detailPackageId =:rentalPackageId or 0 =:rentalPackageId) " +
            "and (p.bannerId =:bannerId or 0=:bannerId) " +
            "and (lower(p.transactionCode) like lower(concat('%', :code ,'%')) or :code is null or :code ='')" +
            "and  (lower(p.orderType) like lower(concat('%', :type ,'%')) or :type is null or :type ='') ")
    Page<Payment> getListPaymentFilter(Pageable pageable, @Param("recruiterId") long recruiterId,@Param("rentalPackageId") long rentalPackageId,
                                       @Param("bannerId") long bannerId,@Param("code") String transactionCode,@Param("type") String orderType);

    @Query(value = "select * from Payment p order by created_at desc    ", nativeQuery = true)
    List<Payment> getListPaymentOrderByDate();

    List<Payment> findByRecruiterIdAndExpiredStatusFalse(long recId);

    @Query(value = "select * from payment p where p.created_at between :startDate and :endDate", nativeQuery = true)
    Page<Payment> getRevenueInMonth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query(value = "select * from payment p where p.created_at between :startDate and :endDate", nativeQuery = true)
    List<Payment> getRevenueInMonthExporter(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Payment findByRecruiterIdAndBannerId(long recId, long bannerId);

    Payment findByRecruiterIdAndDetailPackageId(long recId, long detailPackageId);

    @Query(value = "select sum(dp.max_cv_view)  from payment p join detail_package dp on dp.id = p.detail_package_id  where p.recruiter_id = ?1 ", nativeQuery = true)
    Integer countByTotalCvView(long recId);

    @Query(value = "select * from payment p where p.banner_id > 0", nativeQuery = true)
    List<Payment> getPaymentBanner();

    Payment findByIdAndRecruiterId(long id, long recruiterId);

    @Query(value = "select distinct p.jobId from Payment p where p.jobId > 0 and p.expiredStatus = false ")
    List <Long> getListJobIdInPayment();

    List<Payment> findByBannerIdAndExpiredStatusIsFalse(long bannerId);

    List<Payment> findByDetailPackageIdAndExpiredStatusIsFalse(long detailPackageId);

    Optional<Payment> findByJobId(long jobId);

}
