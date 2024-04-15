package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.recruiter.BannerPositionDetailResponse;
import fpt.edu.capstone.entity.BannerActive;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.BannerActiveRepository;
import fpt.edu.capstone.service.BannerActiveService;
import fpt.edu.capstone.utils.Enums;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BannerActiveImpl implements BannerActiveService {

    private final BannerActiveRepository bannerActiveRepository;

    @Override
    public List<BannerActive> getBannersByPosition(String displayPosition) {
        String approvalStatus = Enums.ApprovalStatus.APPROVED.getStatus();
        return bannerActiveRepository.getBannerByPosition(displayPosition, approvalStatus);
    }

    @Override
    public List<BannerActive> getAllByPaymentId(long paymentId) {
        return bannerActiveRepository.getAllByPaymentId(paymentId);
    }

    @Override
    public List<BannerPositionDetailResponse> getAllBannerByPaymentId(long paymentId) {
        return bannerActiveRepository.getBannerActiveByPaymentId(paymentId);
    }

    @Override
    public BannerActive findByPaymentIdAndPosition(long paymentId, String position) {
        return bannerActiveRepository.findByPaymentIdAndDisplayPosition(paymentId, position);
    }

    @Override
    public Page<BannerActive> getAllBannerForApproval(Pageable pageable, String screenName, LocalDateTime from, LocalDateTime to) {
        return bannerActiveRepository.getAllBannerForApproval(pageable, screenName, from, to);
    }

    @Override
    public BannerActive findById(long id) {
        if(!bannerActiveRepository.findById(id).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.BANNER_IMAGE_DOES_NOT_EXIST);
        }
        return bannerActiveRepository.findById(id).get();
    }
}
