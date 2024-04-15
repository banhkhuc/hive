package fpt.edu.capstone.dto.recruiter;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DetailPurchasedPackageResponse {
    DetailPaymentPackageInfoResponse infoPPRes;
    Map<String,BannerPositionDetailResponse> banner;
    JobDetailPurchasedResponse jobPurchasedPPRes;
}
