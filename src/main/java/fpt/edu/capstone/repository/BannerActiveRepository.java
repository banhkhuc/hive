package fpt.edu.capstone.repository;

import fpt.edu.capstone.dto.recruiter.BannerPositionDetailResponse;
import fpt.edu.capstone.entity.BannerActive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerActiveRepository extends JpaRepository<BannerActive, Long> {

    @Query(value = "select * from banner_active ba join payment p on p.id = ba.payment_id  " +
            "where ba.display_position = ?1 and approval_status = ?2 and p.expired_status = false", nativeQuery = true)
    List<BannerActive> getBannerByPosition(String displayPosition, String approvalStatus);

    List<BannerActive> getAllByPaymentId(long paymentId);

    @Query(value = "select display_position as displayPosition, image_url as imageUrl, approval_status as approvalStatus " +
            "from banner_active ba " +
            "where payment_id = ?1", nativeQuery = true)
    List<BannerPositionDetailResponse> getBannerActiveByPaymentId(long paymentId);

    BannerActive findByPaymentIdAndDisplayPosition(long paymentId, String displayPosition);

    @Query(value = "select * from banner_active ba where " +
            "(lower(ba.display_position) like lower(concat('%', :position ,'%')) or :position is null or :position ='') " +
            "and (ba.created_at between :from and :to)", nativeQuery = true)
    Page<BannerActive> getAllBannerForApproval(Pageable pageable, @Param("position") String screenName, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
